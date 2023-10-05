package net.codinux.log.loki

import kotlinx.datetime.Instant
import net.codinux.log.LogAppenderConfig
import net.codinux.log.LogWriterBase
import net.codinux.log.loki.model.Stream
import net.codinux.log.loki.model.StreamBody
import net.codinux.log.loki.web.KtorWebClient
import net.codinux.log.loki.web.WebClient
import net.codinux.log.statelogger.AppenderStateLogger
import net.codinux.log.statelogger.StdOutStateLogger

open class LokiLogWriter(
    config: LokiLogAppenderConfig,
    stateLogger: AppenderStateLogger = StdOutStateLogger(),
    private val webClient: WebClient = KtorWebClient(stateLogger, getLokiPushApiUrl(config.host), config.username, config.password, config.tenantId)
) : LogWriterBase<Stream>(escapeLabelNames(config), stateLogger) {

    companion object {
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

    init {
        mapper.escapeControlCharacters = true
    }


    override fun instantiateMappedRecord() = Stream().apply {
        mapper.mapStaticFields(this.stream)
    }

    override suspend fun mapRecord(
        timestamp: Instant,
        level: String,
        message: String,
        loggerName: String?,
        threadName: String?,
        exception: Throwable?,
        mdc: Map<String, String>?,
        marker: String?,
        ndc: String?
    ): Stream {
        val stream = getMappedRecordObject()

        stream.set(convertTimestamp(timestamp), getLogLine(message, threadName, exception))

        mapper.mapLogEventFields(stream.stream, level, loggerName, threadName, exception, mdc, marker, ndc)

        return stream
    }


    override suspend fun writeRecords(records: List<Stream>): List<Stream> {
        try {
            streamBody.streams = records

            if (webClient.post(streamBody)) {
                return emptyList() // all records successfully send to Loki = no record failed
            }
        } catch (e: Exception) {
            stateLogger.error("Could not write record", e)
        }

        return records // could not send records to Loki, so we failed to insert all records -> all records failed
    }


    private fun convertTimestamp(timestamp: Instant) =
        // pad start as nanosecondsOfSecond does not contain leading zeros
        "${timestamp.epochSeconds}${timestamp.nanosecondsOfSecond.toString().padStart(9, '0')}"

    private fun getLogLine(message: String, threadName: String?, exception: Throwable?): String {
        return "${ if (config.includeThreadName && threadName != null) "[${threadName}] " else ""}${mapper.escapeControlCharacters(message)}${mapper.getStacktrace(exception) ?: ""}"
    }

}
