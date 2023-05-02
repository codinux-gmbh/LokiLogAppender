package net.codinux.log.loki

import net.codinux.log.LogAppenderConfig
import net.codinux.log.LogWriterBase
import net.codinux.log.loki.web.KtorWebClient
import net.codinux.log.loki.web.WebClient
import net.codinux.log.statelogger.AppenderStateLogger
import net.codinux.log.statelogger.StdOutStateLogger

open class LokiLogWriter(
    config: LogAppenderConfig,
    stateLogger: AppenderStateLogger = StdOutStateLogger(),
    private val webClient: WebClient = KtorWebClient(getLokiPushApiUrl(config.host))
) : LogWriterBase(config, stateLogger) {

    companion object {
        private const val JsonContentType = "application/json"

        fun getLokiPushApiUrl(host: String): String =
            host + (if (host.endsWith('/')) "" else "/") + "loki/api/v1/push"
    }


    protected open val hostNameLabel: String?

    protected open val serializedKubernetesInfo: Array<String>

    init {
        hostNameLabel = mapLabel(config.includeHostName, config.hostNameFieldName, processData.hostName)

        serializedKubernetesInfo = mapKubernetesInfoLabels()
    }


    override fun serializeRecord(
        timestampMillisSinceEpoch: Long,
        timestampMicroAndNanosecondsPart: Long?,
        level: String,
        message: String,
        loggerName: String,
        threadName: String,
        exception: Throwable?,
        mdc: Map<String, String>?,
        marker: String?,
        ndc: String?
    ): String {
        val serializedRecord = StringBuilder()

        serializedRecord.append("""{"stream":{""")

        serializedRecord.append(getIncludedFields(level, loggerName, mdc, marker, ndc).joinToString(","))

        serializedRecord.append("""},"values":[""")
        serializedRecord.append("""["${convertTimestamp(timestampMillisSinceEpoch, timestampMicroAndNanosecondsPart)}",
            |"${getLogLine(message, threadName, exception)}"]""".trimMargin())

        serializedRecord.append("]}")

        return serializedRecord.toString()
    }


    override suspend fun writeRecords(records: List<String>): List<String> {
        try {
            val requestBody = createRequestBody(records)

            // TODO: GZip body and add "Content-Encoding: gzip" header
            if (webClient.post("", requestBody, JsonContentType)) {
                return emptyList() // all records successfully send to Loki
            }
        } catch (e: Exception) {
            stateLogger.error("Could not write record", e)
        }

        return records // could not send records to Loki, so we failed to insert all records -> all records failed
    }

    protected open fun createRequestBody(serializedRecords: List<String>): String {
        // it's not that easy to build the request body with a standard JSON serializer, so let's build the JSON request body ourselves
        // for the format see https://grafana.com/docs/loki/latest/api/#push-log-entries-to-loki
        val body = StringBuilder()
            .append("""{"streams":[""")

        serializedRecords.forEachIndexed { index, record ->
            body.append(record)

            if (index < serializedRecords.size - 1) {
                body.append(',')
            }
        }

        body.append("]}")

        return body.toString()
    }


    private fun convertTimestamp(timestampMillisSinceEpoch: Long, timestampMicroAndNanosecondsPart: Long?) =
        timestampMillisSinceEpoch * 1_000_000 + (timestampMicroAndNanosecondsPart ?: 0)

    private fun getLogLine(message: String, threadName: String, exception: Throwable?): String {
        val logLine = StringBuilder()

        if (config.includeThreadName) {
            logLine.append("[${threadName}] ")
        }

        logLine.append(cleanFieldValue(message))

        if (config.includeStacktrace && exception != null) {
            logLine.append(" ${cleanFieldValue(extractStacktrace(exception))}")
        }

        return logLine.toString()
    }

    protected open fun getIncludedFields(level: String, loggerName: String, mdc: Map<String, String>?, marker: String?, ndc: String?): List<String> = mapIncludedFields(
        mapLabel(config.includeLogLevel, config.logLevelFieldName, level),
        mapLabel(config.includeLoggerName, config.loggerNameFieldName, loggerName),
        mapLabel(config.includeLoggerClassName, config.loggerClassNameFieldName) { extractLoggerClassName(loggerName) },

        hostNameLabel,
        mapLabel(config.includeAppName, config.appNameFieldName, config.appName),

        *mapMdcLabel(config.includeMdc && mdc != null, mdc),
        mapLabel(config.includeMarker && marker != null, config.markerFieldName, marker),
        mapLabel(config.includeNdc && ndc != null, config.ndcFieldName, ndc),

        *mapKubernetesInfoLabels()
    )

    private fun mapIncludedFields(vararg labels: String?): List<String> =
        labels.filterNotNull()

    protected open fun mapLabel(includeField: Boolean, fieldName: String, valueSupplier: () -> Any?): String? =
        if (includeField) {
            mapLabel(includeField, fieldName, valueSupplier.invoke())
        } else {
            null
        }

    protected open fun mapLabel(includeField: Boolean, fieldName: String, value: Any?): String? =
        if (includeField) {
            """"${cleanLabelName(fieldName)}":"${value ?: "null"}""""
        } else {
            null
        }

    protected open fun mapLabelIfNotNull(fieldName: String, value: Any?): String? =
        mapLabel(value != null, fieldName, value)

    private fun mapMdcLabel(includeMdc: Boolean, mdc: Map<String, String>?): Array<String> {
        if (includeMdc) {
            mdc?.let {
                val prefix = determinePrefix(config.mdcKeysPrefix)

                return mdc.mapNotNull { (key, value) ->
                    mapLabel(includeMdc, prefix + key, value)
                }.toTypedArray()
            }
        }

        return emptyArray()
    }

    private fun mapKubernetesInfoLabels(): Array<String> {
        if (config.includeKubernetesInfo) {
            kubernetesInfo?.let { info ->
                val prefix = determinePrefix(config.kubernetesFieldsPrefix)
                val labels = mutableListOf<String?>()

                labels.add(mapLabel(true, prefix + "namespace", info.namespace))
                labels.add(mapLabel(true, prefix + "podName", info.podName))
                labels.add(mapLabel(true, prefix + "podIp", info.podIp))
                labels.add(mapLabel(true, prefix + "startTime", info.startTime))

                labels.add(mapLabelIfNotNull(prefix + "podUid", info.podUid))
                labels.add(mapLabelIfNotNull(prefix + "restartCount", info.restartCount))
                labels.add(mapLabelIfNotNull(prefix + "containerName", info.containerName))
                labels.add(mapLabelIfNotNull(prefix + "containerId", info.containerId))
                labels.add(mapLabelIfNotNull(prefix + "imageName", info.imageName))
                labels.add(mapLabelIfNotNull(prefix + "imageId", info.imageId))
                labels.add(mapLabelIfNotNull(prefix + "nodeIp", info.nodeIp))
                labels.add(mapLabelIfNotNull(prefix + "node", info.nodeName))

                return labels.filterNotNull().toTypedArray()
            }
        }

        return emptyArray()
    }

    /**
     * It may contain ASCII letters and digits, as well as underscores and colons. It must match the regex [a-zA-Z_:][a-zA-Z0-9_:]*.
     *
     * Note: The colons are reserved for user defined recording rules. They should not be used by exporters or direct instrumentation.
     *
     * (And at least for Prometheus labels may not start with an underscore.)
     *
     * Label values may contain any Unicode characters.
     *
     * A label with an empty label value is considered equivalent to a label that does not exist.
     */

    // And:

    /**
     * Labels are the index to Loki’s log data. They are used to find the compressed log content, which is stored separately as chunks. Every unique combination of label and values defines a stream, and logs for a stream are batched up, compressed, and stored as chunks.
     *
     * For Loki to be efficient and cost-effective, we have to use labels responsibly.
     *
     * (https://grafana.com/docs/loki/latest/fundamentals/labels/)
     */

    // Therefore:
    // Do not use labels to store dimensions with high cardinality (many different label values), such as user IDs, email addresses, or other unbounded sets of values.
    // (https://prometheus.io/docs/practices/naming/)
    //
    // or:
    // Imagine now if you set a label for ip. Not only does every request from a user become a unique stream. Every request with a
    // different action or status_code from the same user will get its own stream.

    /**
     * Doing some quick math, if there are maybe four common actions (GET, PUT, POST, DELETE) and maybe four common status codes (although there could be more than four!), this would be 16 streams and 16 separate chunks. Now multiply this by every user if we use a label for ip. You can quickly have thousands or tens of thousands of streams.
     *
     * This is high cardinality. This can kill Loki.
     *
     * High cardinality causes Loki to build a huge index (read: $$$$) and to flush thousands of tiny chunks to the object store (read: slow).
     * Loki currently performs very poorly in this configuration and will be the least cost-effective and least fun to run and use.
     *
     * (https://grafana.com/docs/loki/latest/fundamentals/labels/)
     */

    /**
     *
     * As we see people using Loki who are accustomed to other index-heavy solutions (..)
     * Large indexes are complicated and expensive. Often a full-text index of your log data is the same size or bigger than the log data itself.
     * To query your log data, you need this index loaded, and for performance, it should probably be in memory. This is difficult to scale, and
     * as you ingest more logs, your index gets larger quickly.
     *
     * Now let’s talk about Loki, where the index is typically an order of magnitude smaller than your ingested log volume. So if you are doing
     * a good job of keeping your streams and stream churn to a minimum, the index grows very slowly compared to the ingested logs.
     *
     * When using Loki, you may need to forget what you know and look to see how the problem can be solved differently with parallelization.
     * Loki’s superpower is breaking up queries into small pieces and dispatching them in parallel so that you can query huge amounts of log data in small amounts of time.
     *
     * (https://grafana.com/docs/loki/latest/fundamentals/labels/)
     */
    private fun cleanLabelName(fieldName: String): String {
        // TODO: implement removing illegal characters by converting illegal characters to underscore. Do this once and set it in LoggerSettings
        return fieldName.replace(' ', '_')
    }

    @JvmName("cleanFieldValueNullable")
    private fun cleanFieldValue(value: String?): String? =
        value?.let { cleanFieldValue(it) }

    private fun cleanFieldValue(value: String): String =
        // we have to escape single backslashes as Loki doesn't accept control characters
        // (returns then 400 Bad Request invalid control character found: 10, error found in #10 byte of ...)
        value.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t")
            .replace("\"", "\\\"")

    private fun determinePrefix(prefix: String?): String =
        if (prefix.isNullOrBlank()) "" else prefix + "_" // TODO: in Loki prefixes get separated by '_', in ElasticSearch by '.'

    // loggerName is in most cases full qualified class name including packages, try to extract only name of class
    protected open fun extractLoggerClassName(loggerName: String): String { // TODO: as there's only a small amount of loggers: Cache extracted logger class names
        val indexOfDot = loggerName.lastIndexOf('.')
        if (indexOfDot >= 0) {
            return loggerName.substring(indexOfDot + 1)
        }

        return loggerName
    }

    protected open fun extractStacktrace(exception: Throwable?): String? {
        return exception?.let {
            val stackTrace = exception.stackTraceToString()

            if (stackTrace.length > config.stacktraceMaxFieldLength) {
                stackTrace.substring(0, config.stacktraceMaxFieldLength)
            } else {
                stackTrace
            }
        }
    }

}
