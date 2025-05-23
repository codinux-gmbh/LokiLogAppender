package net.codinux.log.loki

import net.codinux.log.LogRecord
import net.codinux.log.LogWriterBase
import net.codinux.log.config.LogAppenderConfig
import net.codinux.log.data.ProcessData
import net.codinux.log.loki.config.LokiLogAppenderConfig
import net.codinux.log.loki.model.Stream
import net.codinux.log.loki.model.StreamBody
import net.codinux.log.loki.util.LokiLabelEscaper
import net.codinux.log.loki.web.WebClient
import net.codinux.log.statelogger.AppenderStateLogger
import net.codinux.log.statelogger.StdOutStateLogger
import net.dankito.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

open class LokiLogWriter(
    config: LokiLogAppenderConfig,
    stateLogger: AppenderStateLogger = StdOutStateLogger.Default,
    private val webClient: WebClient,
    processData: ProcessData? = null,
    logErrorMessagesAtMaximumOncePer: Duration = 5.minutes,
) : LogWriterBase<Stream>(escapeLabelNames(config), stateLogger, LokiLogRecordMapper(config.fields), processData, logErrorMessagesAtMaximumOncePer) {

    companion object {
        private val labelEscaper = LokiLabelEscaper.Default

        fun getLokiPushApiUrl(host: String): String =
            host + (if (host.endsWith('/')) "" else "/") + "loki/api/v1/push"

        // TODO: add to LokiLogAppenderConfig

        fun escapeLabelNames(config: LogAppenderConfig) =
            labelEscaper.escapeLabelNames(config)
    }


    protected open val streamBody = StreamBody()


    override fun instantiateMappedRecord() = LogRecord(Stream().apply {
        mapper.mapStaticFields(this.stream)
    })

    override suspend fun mapRecord(record: LogRecord<Stream>) {
        record.mappedRecord.set(convertTimestamp(record.timestamp), getLogLine(record), getStructuredMetadata(record))

        mapper.mapLogEventFields(record, record.mappedRecord.stream)
    }


    override suspend fun writeRecords(records: List<LogRecord<Stream>>): List<LogRecord<Stream>> {
        try {
            streamBody.streams = records.map { it.mappedRecord }

            val (status, responseBody) = webClient.post(streamBody, records.size == 1)

            if (status in (200 until 300)) {
                return emptyList() // all records successfully send to Loki = no record failed
            } else if (status == 400) { // Bad request -> try to find the culprit and store at least all other ones
                if (records.size > 1) {
                    // write records one by one, so that the problem free records succeed and only the erroneous ones remain
                    return records.flatMap { record ->
                        writeRecords(listOf(record))
                    }
                } else if (records.size == 1) { // we sent records one by one
                    stateLogger.error("Could not push logs to Loki: $status $responseBody. Request body was:\n$streamBody", // TODO: map streamBody to JSON
                        logAtMaximumEach = logErrorMessagesAtMaximumOncePer, category = "$status $responseBody", e = null)

                    // we're not able to send this record successfully to Loki, giving up
                    return handleFailedRecord(records.first())
                }
            }
        } catch (e: Exception) {
            stateLogger.error("Could not write record", e, logErrorMessagesAtMaximumOncePer)
        }

        return records // could not send records to Loki, so we failed to insert all records -> all records failed
    }

    protected open fun handleFailedRecord(record: LogRecord<Stream>): List<LogRecord<Stream>> {
        stateLogger.warn("Dropping record as Loki indicated bad request: ${record.mappedRecord}")

        return emptyList()
    }


    protected open fun convertTimestamp(timestamp: Instant) =
        // pad start as nanosecondsOfSecond does not contain leading zeros
        "${timestamp.epochSeconds}${timestamp.nanosecondsOfSecond.toString().padStart(9, '0')}"

    protected open fun getLogLine(record: LogRecord<Stream>): String = with (record) {
        return "${ if (config.fields.includeThreadName && threadName != null) "[${threadName}] " else ""}${mapper.escapeControlCharacters(message)}${mapper.getStacktrace(exception) ?: ""}"
    }

    protected open fun getStructuredMetadata(record: LogRecord<Stream>): Map<String, String> = emptyMap()

}
