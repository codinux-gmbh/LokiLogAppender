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
) : LogWriterBase<Stream>(config, stateLogger) {

    companion object {
        private const val JsonContentType = "application/json"

        fun getLokiPushApiUrl(host: String): String =
            host + (if (host.endsWith('/')) "" else "/") + "loki/api/v1/push"
    }


    protected open val streamBody = StreamBody()

    protected open val cachedStreams = Channel<Stream>(config.maxBufferedLogRecords)

    protected open val cachedStackTraces = mutableMapOf<Int?, String>() // TODO: use thread safe Map

    protected open val cachedLoggerClassNames = mutableMapOf<String, String>() // TODO: use thread safe Map

    init {
        // pre-cache Streams
        senderScope.launch {
            IntRange(0, min(1_000, config.maxBufferedLogRecords / 2)).forEach {
                cachedStreams.send(createStreamObject())
            }
        }
    }


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
        val stream = getStreamObject()

        stream.set(convertTimestamp(timestamp), getLogLine(message, threadName, exception))

        mapLabels(stream.stream, level, loggerName, threadName, exception, mdc, marker, ndc)

        return stream
    }

    private suspend fun getStreamObject(): Stream {
        return if (cachedStreams.isNotEmpty) {
            cachedStreams.receive()
        } else {
            createStreamObject()
        }
    }

    private fun createStreamObject() = Stream().apply {
        mapStaticLabels(this.stream)
    }


    override suspend fun writeRecords(records: List<Stream>): List<Stream> {
        try {
            streamBody.streams = records

            if (webClient.post("", streamBody, JsonContentType)) {
                records.forEach {
                    cachedStreams.send(it)
                }

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
    private fun mapStaticLabels(labels: MutableMap<String, String?>) {
        mapLabel(labels, config.includeHostName, config.hostNameFieldName, processData.hostName)
        mapLabel(labels, config.includeAppName, config.appNameFieldName, config.appName)

        // TODO: PodInfo may hasn't been initialized yet at this point
        mapPodInfoLabels(labels)

        // pre-allocate per log event labels in Map
        mapLabels(labels, "", "", "", null, null, null, null)
    }

    private fun mapLabels(
        labels: MutableMap<String, String?>,
        level: String,
        loggerName: String,
        threadName: String,
        exception: Throwable?,
        mdc: Map<String, String>?,
        marker: String?,
        ndc: String?
    ) {
        mapLabel(labels, config.includeLogLevel, config.logLevelFieldName, level)
        mapLabel(labels, config.includeLoggerName, config.loggerNameFieldName, loggerName)
        mapLabel(labels, config.includeLoggerClassName, config.loggerClassNameFieldName) { extractLoggerClassName(loggerName) }

        // TODO: these are the only dynamic fields. Remember these so that they can be removed again from Map (where? after successful writing? Before setting the next record?)
        mapMdcLabel(labels, config.includeMdc && mdc != null, mdc)
        mapLabel(labels, config.includeMarker && marker != null, config.markerFieldName, marker)
        mapLabel(labels, config.includeNdc && ndc != null, config.ndcFieldName, ndc)
    }

    protected open fun mapLabel(labels: MutableMap<String, String?>, includeField: Boolean, fieldName: String, valueSupplier: () -> String?) {
        if (includeField) {
            mapLabel(labels, includeField, fieldName, valueSupplier.invoke())
        }
    }

    protected open fun mapLabel(labels: MutableMap<String, String?>, includeField: Boolean, fieldName: String, value: String?) {
        if (includeField) {
            labels[escapeLabelName(fieldName)] = value
        }
    }

    protected open fun mapLabelIfNotNull(labels: MutableMap<String, String?>, fieldName: String, value: String?) {
        mapLabel(labels, value != null, fieldName, value)
    }

    private fun mapMdcLabel(labels: MutableMap<String, String?>, includeMdc: Boolean, mdc: Map<String, String>?) {
        if (includeMdc) {
            mdc?.let {
                val prefix = determinePrefix(config.mdcKeysPrefix)

                mdc.mapNotNull { (key, value) ->
                    mapLabel(labels, includeMdc, prefix + key, value)
                }
            }
        }
    }

    private fun mapPodInfoLabels(labels: MutableMap<String, String?>) {
        if (config.includeKubernetesInfo) {
            podInfo?.let { info ->
                val prefix = determinePrefix(config.kubernetesFieldsPrefix)

                mapLabel(labels, true, prefix + "namespace", info.namespace)
                mapLabel(labels, true, prefix + "podName", info.podName)
                mapLabel(labels, true, prefix + "podIp", info.podIp)
                mapLabel(labels, true, prefix + "startTime", info.startTime)

                mapLabelIfNotNull(labels, prefix + "podUid", info.podUid)
                mapLabelIfNotNull(labels, prefix + "restartCount", info.restartCount.toString())
                mapLabelIfNotNull(labels, prefix + "containerName", info.containerName)
                mapLabelIfNotNull(labels, prefix + "containerId", info.containerId)
                mapLabelIfNotNull(labels, prefix + "imageName", info.imageName)
                mapLabelIfNotNull(labels, prefix + "imageId", info.imageId)
                mapLabelIfNotNull(labels, prefix + "nodeIp", info.nodeIp)
                mapLabelIfNotNull(labels, prefix + "node", info.nodeName)
            }
        }
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
     * Labels are the index to Loki’s log data. They are used to find the compressed log content, which is stored separately as chunks.
     * Every unique combination of label and values defines a stream, and logs for a stream are batched up, compressed, and stored as chunks.
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
    private fun escapeLabelName(fieldName: String): String {
        // TODO: implement removing illegal characters by converting illegal characters to underscore. Do this once and set it in LoggerSettings
        return fieldName.replace(' ', '_')
    }

    private fun escapeFieldValue(value: String): String =
        // we have to escape single backslashes as Loki doesn't accept control characters
        // (returns then 400 Bad Request invalid control character found: 10, error found in #10 byte of ...)
        value.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t")
            .replace("\"", "\\\"")

    private fun determinePrefix(prefix: String?): String =
        if (prefix.isNullOrBlank()) "" else prefix + "_" // TODO: in Loki prefixes get separated by '_', in ElasticSearch by '.'

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
