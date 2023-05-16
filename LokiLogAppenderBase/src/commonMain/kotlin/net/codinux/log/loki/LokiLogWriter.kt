package net.codinux.log.loki

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import net.codinux.log.LogAppenderConfig
import net.codinux.log.LogWriterBase
import net.codinux.log.extensions.isNotEmpty
import net.codinux.log.loki.model.Stream
import net.codinux.log.loki.model.StreamBody
import net.codinux.log.loki.web.KtorWebClient
import net.codinux.log.loki.web.WebClient
import net.codinux.log.statelogger.AppenderStateLogger
import net.codinux.log.statelogger.StdOutStateLogger
import kotlin.math.min

open class LokiLogWriter(
    config: LogAppenderConfig,
    stateLogger: AppenderStateLogger = StdOutStateLogger(),
    private val webClient: WebClient = KtorWebClient(getLokiPushApiUrl(config.host), config.username, config.password, (config as? LokiLogAppenderConfig)?.tenantId)
) : LogWriterBase<Stream>(escapeLabelNames(config), stateLogger) {

    companion object {
        private const val JsonContentType = "application/json"

        fun getLokiPushApiUrl(host: String): String =
            host + (if (host.endsWith('/')) "" else "/") + "loki/api/v1/push"

        fun escapeLabelNames(config: LogAppenderConfig): LogAppenderConfig {
            config.logLevelFieldName = escapeLabelName(config.logLevelFieldName)
            config.loggerNameFieldName = escapeLabelName(config.loggerNameFieldName)
            config.loggerClassNameFieldName = escapeLabelName(config.loggerClassNameFieldName)
            config.threadNameFieldName = escapeLabelName(config.threadNameFieldName)

            config.hostNameFieldName = escapeLabelName(config.hostNameFieldName)
            config.hostIpFieldName = escapeLabelName(config.hostIpFieldName)
            config.appNameFieldName = escapeLabelName(config.appNameFieldName)
            config.stacktraceFieldName = escapeLabelName(config.stacktraceFieldName)

            config.mdcKeysPrefix = determinePrefix(config.mdcKeysPrefix)

            config.markerFieldName = escapeLabelName(config.markerFieldName)
            config.ndcFieldName = escapeLabelName(config.ndcFieldName)

            config.kubernetesFieldsPrefix = determinePrefix(config.kubernetesFieldsPrefix)
            config.kubernetesLabelsPrefix = determinePrefix(config.kubernetesLabelsPrefix)
            config.kubernetesAnnotationsPrefix = determinePrefix(config.kubernetesAnnotationsPrefix)

            return config
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
         *
         * And:
         *
         * Labels are the index to Loki’s log data. They are used to find the compressed log content, which is stored separately as chunks.
         * Every unique combination of label and values defines a stream, and logs for a stream are batched up, compressed, and stored as chunks.
         *
         * For Loki to be efficient and cost-effective, we have to use labels responsibly.
         *
         * (https://grafana.com/docs/loki/latest/fundamentals/labels/)
         *
         *
         * Therefore:
         * Do not use labels to store dimensions with high cardinality (many different label values), such as user IDs, email addresses, or other unbounded sets of values.
         * (https://prometheus.io/docs/practices/naming/)
         *
         * or:
         * Imagine now if you set a label for ip. Not only does every request from a user become a unique stream. Every request with a
         * different action or status_code from the same user will get its own stream.
         *
         *
         * Doing some quick math, if there are maybe four common actions (GET, PUT, POST, DELETE) and maybe four common status codes
         * (although there could be more than four!), this would be 16 streams and 16 separate chunks. Now multiply this by every user
         * if we use a label for ip. You can quickly have thousands or tens of thousands of streams.
         *
         * This is high cardinality. This can kill Loki.
         *
         * High cardinality causes Loki to build a huge index (read: $$$$) and to flush thousands of tiny chunks to the object store (read: slow).
         * Loki currently performs very poorly in this configuration and will be the least cost-effective and least fun to run and use.
         *
         * (https://grafana.com/docs/loki/latest/fundamentals/labels/)
         *
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
        private fun escapeLabelName(fieldName: String): String {
            // TODO: implement removing illegal characters by converting illegal characters to underscore. Do this once and set it in LoggerSettings
            return fieldName.replace(' ', '_')
        }

        private fun determinePrefix(prefix: String?): String =
            if (prefix.isNullOrBlank()) ""
            else if (prefix.endsWith('.')) prefix.substring(0, prefix.length - 2) + "_" // in Loki prefixes get separated by '_', in ElasticSearch by '.'
            else if (prefix.endsWith('_') == false) prefix + "_"
            else prefix + "_"
    }


    protected open val streamBody = StreamBody()

    protected open val cachedStackTraces = mutableMapOf<Int?, String>() // TODO: use thread safe Map

    protected open val cachedLoggerClassNames = mutableMapOf<String, String>() // TODO: use thread safe Map


    override suspend fun mapRecord(
        timestamp: Instant,
        level: String,
        message: String,
        loggerName: String,
        threadName: String,
        exception: Throwable?,
        mdc: Map<String, String>?,
        marker: String?,
        ndc: String?
    ): Stream {
        val stream = getMappedRecordObject()

        stream.set(convertTimestamp(timestamp), getLogLine(message, threadName, exception))

        mapLabels(stream, level, loggerName, threadName, exception, mdc, marker, ndc)

        return stream
    }

    override fun instantiateMappedRecord() = Stream().apply {
        mapStaticLabels(this)
    }


    override suspend fun writeRecords(records: List<Stream>): List<Stream> {
        try {
            streamBody.streams = records

            if (webClient.post("", streamBody, JsonContentType)) {
                releaseMappedRecords(records)

                return emptyList() // all records successfully send to Loki
            }
        } catch (e: Exception) {
            stateLogger.error("Could not write record", e)
        }

        return records // could not send records to Loki, so we failed to insert all records -> all records failed
    }


    private fun convertTimestamp(timestamp: Instant) =
        // pad start as nanosecondsOfSecond does not contain leading zeros
        "${timestamp.epochSeconds}${timestamp.nanosecondsOfSecond.toString().padStart(9, '0')}"

    private fun getLogLine(message: String, threadName: String, exception: Throwable?): String {
        return "${ if (config.includeThreadName) "[${threadName}] " else ""}${escapeFieldValue(message)}${getStacktrace(exception) ?: ""}"
    }

    /**
     * Add labels that never change during the whole process lifetime
     */
    private fun mapStaticLabels(stream: Stream) {
        mapLabel(stream, config.includeHostName, config.hostNameFieldName, processData.hostName)
        mapLabel(stream, config.includeAppName, config.appNameFieldName, config.appName)

        mapPodInfoLabels(stream)

        // pre-allocate per log event labels in Map
        mapLabels(stream, "", "", "", null, null, null, null)
    }

    private fun mapLabels(
        stream: Stream,
        level: String,
        loggerName: String,
        threadName: String,
        exception: Throwable?,
        mdc: Map<String, String>?,
        marker: String?,
        ndc: String?
    ) {
        stream.dynamicLabels.forEach { dynamicLabel ->
            stream.stream.remove(dynamicLabel)
        }
        stream.dynamicLabels.clear()

        mapLabel(stream, config.includeLogLevel, config.logLevelFieldName, level)
        mapLabel(stream, config.includeLoggerName, config.loggerNameFieldName, loggerName)
        mapLabel(stream, config.includeLoggerClassName, config.loggerClassNameFieldName) { extractLoggerClassName(loggerName) }

        mapMdcLabels(stream, config.includeMdc && mdc != null, mdc)
        mapLabel(stream, config.includeMarker, config.markerFieldName, marker)
        mapLabel(stream, config.includeNdc, config.ndcFieldName, ndc)
    }

    protected open fun mapLabel(stream: Stream, includeField: Boolean, fieldName: String, valueSupplier: () -> String?) {
        if (includeField) {
            mapLabel(stream, includeField, fieldName, valueSupplier.invoke())
        }
    }

    protected open fun mapLabel(stream: Stream, includeField: Boolean, fieldName: String, value: String?) {
        if (includeField) {
            stream.stream[fieldName] = value
        }
    }

    protected open fun mapLabelIfNotNull(stream: Stream, fieldName: String, value: String?) {
        mapLabel(stream, value != null, fieldName, value)
    }

    protected open fun addDynamicLabel(stream: Stream, fieldName: String, value: String?) {
        // Remember labels that are not included in each log record, like MDC values, and remove them on next log record mapping
        stream.dynamicLabels.add(fieldName)

        mapLabel(stream, true, fieldName, value)
    }

    private fun mapMdcLabels(stream: Stream, includeMdc: Boolean, mdc: Map<String, String>?) {
        if (includeMdc) {
            if (mdc != null) {
                val prefix = config.mdcKeysPrefix

                mdc.mapNotNull { (key, value) ->
                    addDynamicLabel(stream, prefix + key, value)
                }
            }
        }
    }

    private fun mapPodInfoLabels(stream: Stream) {
        if (config.includeKubernetesInfo) {
            podInfo?.let { info ->
                val prefix = config.kubernetesFieldsPrefix

                mapLabel(stream, true, prefix + "namespace", info.namespace)
                mapLabel(stream, true, prefix + "podName", info.podName)
                mapLabel(stream, true, prefix + "podIp", info.podIp)
                mapLabel(stream, true, prefix + "startTime", info.startTime)

                mapLabelIfNotNull(stream, prefix + "podUid", info.podUid)
                mapLabelIfNotNull(stream, prefix + "restartCount", info.restartCount.toString())
                mapLabelIfNotNull(stream, prefix + "containerName", info.containerName)
                mapLabelIfNotNull(stream, prefix + "containerId", info.containerId)
                mapLabelIfNotNull(stream, prefix + "imageName", info.imageName)
                mapLabelIfNotNull(stream, prefix + "imageId", info.imageId)
                mapLabelIfNotNull(stream, prefix + "nodeIp", info.nodeIp)
                mapLabelIfNotNull(stream, prefix + "node", info.nodeName)
            }
        }
    }

    private fun escapeFieldValue(value: String): String =
        // we have to escape single backslashes as Loki doesn't accept control characters
        // (returns then 400 Bad Request invalid control character found: 10, error found in #10 byte of ...)
        value.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t")
            .replace("\"", "\\\"")

    // loggerName is in most cases full qualified class name including packages, try to extract only name of class
    protected open fun extractLoggerClassName(loggerName: String): String {
        cachedLoggerClassNames[loggerName]?.let {
            return it
        }

        val indexOfDot = loggerName.lastIndexOf('.')

        val loggerClassName = if (indexOfDot >= 0) {
            loggerName.substring(indexOfDot + 1)
        } else {
            loggerName
        }

        cachedLoggerClassNames[loggerName] = loggerClassName

        return loggerClassName
    }

    protected open fun getStacktrace(exception: Throwable?): String? {
        if (config.includeStacktrace == false) {
            return null
        }

        return exception?.let {
            // Throwable doesn't implement hashCode(), so it differs for each new object -> create a hash code to uniquely identify Throwables
            val exceptionHash = getExceptionHashCode(exception)

            // exception.stackTraceToString() is one of the most resource intensive operations of our implementation. As there typically aren't
            // that much different exceptions in an application, we cache the stack trace of each unique exception for performance reasons.
            cachedStackTraces[exceptionHash]?.let {
                return it
            }

            val stackTrace = escapeFieldValue(extractStacktrace(exception))

            cachedStackTraces[exceptionHash] = stackTrace

            stackTrace
        }
    }

    protected open fun extractStacktrace(exception: Throwable): String {
        val stackTrace = exception.stackTraceToString()

        return if (stackTrace.length > config.stacktraceMaxFieldLength) {
            stackTrace.substring(0, config.stacktraceMaxFieldLength)
        } else {
            stackTrace
        }
    }

    private fun getExceptionHashCode(exception: Throwable): Int {
        var hashCode = exception::class.hashCode()

        if (exception.message != null) {
            hashCode = 31 * hashCode + exception.message.hashCode()
        }

        // to avoid infinite loops check if exception.cause and exception equal
        if (exception.cause != null && exception.cause != exception) {
            hashCode = 31 * hashCode + getExceptionHashCode(exception.cause!!)
        }

        return hashCode
    }

}
