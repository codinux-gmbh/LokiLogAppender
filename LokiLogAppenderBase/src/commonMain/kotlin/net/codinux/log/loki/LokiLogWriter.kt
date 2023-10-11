package net.codinux.log.loki

import kotlinx.datetime.Instant
import net.codinux.log.LogWriterBase
import net.codinux.log.config.KubernetesFieldsConfig
import net.codinux.log.config.LogAppenderConfig
import net.codinux.log.loki.config.LokiLogAppenderConfig
import net.codinux.log.loki.model.Stream
import net.codinux.log.loki.model.StreamBody
import net.codinux.log.loki.util.LokiLabelEscaper
import net.codinux.log.loki.web.KtorWebClient
import net.codinux.log.loki.web.WebClient
import net.codinux.log.statelogger.AppenderStateLogger
import net.codinux.log.statelogger.StdOutStateLogger

open class LokiLogWriter(
    config: LokiLogAppenderConfig,
    stateLogger: AppenderStateLogger = StdOutStateLogger(),
    private val webClient: WebClient = KtorWebClient(stateLogger, getLokiPushApiUrl(config.writer.hostUrl), config.tenantId, config.writer)
) : LogWriterBase<Stream>(escapeLabelNames(config), stateLogger) {

    companion object {
        fun getLokiPushApiUrl(host: String): String =
            host + (if (host.endsWith('/')) "" else "/") + "loki/api/v1/push"

        // TODO: add to LokiLogAppenderConfig

        fun escapeLabelNames(config: LogAppenderConfig) =
            LokiLabelEscaper().escapeLabelNames(config)
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
        return "${ if (config.fields.includeThreadName && threadName != null) "[${threadName}] " else ""}${mapper.escapeControlCharacters(message)}${mapper.getStacktrace(exception) ?: ""}"
    }

}
