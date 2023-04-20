package net.codinux.log.loki

import net.codinux.log.LogRecord
import net.codinux.log.LoggerSettings
import net.codinux.log.loki.web.KtorWebClient
import net.codinux.log.loki.web.WebClient

open class LokiLogWriter(
    private val settings: LoggerSettings,
    private val webClient: WebClient = KtorWebClient(getLokiPushApiUrl(settings.host))
) {

    companion object {
        private const val JsonContentType = "application/json"

        fun getLokiPushApiUrl(host: String): String =
            host + (if (host.endsWith('/')) "" else "/") + "loki/api/v1/push"
    }

    open suspend fun writeRecord(record: LogRecord) {
        val requestBody = createRequestBody(record)

        // TODO: GZip body and add "Content-Encoding: gzip" header
        webClient.post("", requestBody, JsonContentType)
    }

    protected open fun createRequestBody(record: LogRecord): String {
        // it's not that easy to build the request body with a standard JSON serializer, so let's build the JSON request body ourselves
        // for the format see https://grafana.com/docs/loki/latest/api/#push-log-entries-to-loki
        val body = StringBuilder()
            .append("""{"streams":[""")

        createStreamForLogRecord(body, record)

        body.append("]}")

        return body.toString()
    }

    protected open fun createStreamForLogRecord(body: StringBuilder, record: LogRecord) {
        body.append("""{"stream":{""")

        body.append(getIncludedFields(record).joinToString(","))

        body.append("""},"values":[""")
        body.append("""["${convertTimestamp(record)}","${getLogLine(record)}"]""")

        body.append("]}")
    }

    private fun convertTimestamp(record: LogRecord) =
        record.timestamp.epochSeconds * 1_000_000_000 + record.timestamp.nanosecondsOfSecond

    private fun getLogLine(record: LogRecord): String {
        val logLine = StringBuilder()

        if (settings.includeThreadName) {
            logLine.append("[${record.threadName}] ")
        }

        logLine.append(record.message)

        if (settings.includeStacktrace) {
            logLine.append(" ${extractStacktrace(record)}")
        }

        return logLine.toString()
    }

    protected open fun getIncludedFields(record: LogRecord): List<String> = mapIncludedFields(
        mapLabel(settings.includeLogLevel, settings.logLevelFieldName, record.level),
        mapLabel(settings.includeLoggerName, settings.loggerNameFieldName, record.loggerName),
        mapLabel(settings.includeLoggerClassName, settings.loggerClassNameFieldName) { extractLoggerName(record) },

        mapLabel(settings.includeHost, settings.hostFieldName, record.host),
        mapLabel(settings.includeDeviceName, settings.deviceNameFieldName, settings.deviceName),
        mapLabel(settings.includeAppName, settings.appNameFieldName, settings.appName),

        *mapMdcLabel(settings.includeMdc && record.mdc != null, record.mdc).toTypedArray(),
        mapLabel(settings.includeMarker && record.marker != null, settings.markerFieldName, record.marker),
        mapLabel(settings.includeNdc && record.ndc != null, settings.ndcFieldName, record.ndc),
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
            """"${cleanFieldName(fieldName)}":"${value ?: "null"}""""
        } else {
            null
        }

    private fun mapMdcLabel(includeMdc: Boolean, mdc: Map<String, String>?): List<String> {
        if (includeMdc) {
            mdc?.let {
                val prefix = determinePrefix(settings.mdcKeysPrefix)

                return mdc.mapNotNull { (key, value) ->
                    mapLabel(includeMdc, prefix + key, value)
                }
            }
        }

        return emptyList()
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
    private fun cleanFieldName(fieldName: String): String {
        // TODO: implement removing illegal characters by converting illegal characters to underscore. Do this once and set it in LoggerSettings
        return fieldName
    }

    private fun determinePrefix(prefix: String?): String =
        if (prefix.isNullOrBlank()) "" else prefix + "_" // TODO: in Loki prefixes get separated by '_', in ElasticSearch by '.'

    // loggerName is in most cases full qualified class name including packages, try to extract only name of class
    protected open fun extractLoggerName(record: LogRecord): String { // TODO: as there's only a small amount of loggers: Cache extracted logger names
        var loggerName = record.loggerName

        val indexOfDot = loggerName.lastIndexOf('.')
        if (indexOfDot >= 0) {
            loggerName = loggerName.substring(indexOfDot + 1)
        }

        return loggerName
    }

    protected open fun extractStacktrace(record: LogRecord): String? {
        return record.exception?.let { exception ->
            val stackTrace = exception.stackTraceToString()
                // we have to escape single backslashes as Loki doesn't accept control characters
                // (returns then 400 Bad Request invalid control character found: 10, error found in #10 byte of ...)
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t")

            if (stackTrace.length > settings.stacktraceMaxFieldLength) {
                return stackTrace.substring(0, settings.stacktraceMaxFieldLength)
            } else {
                stackTrace
            }
        }
    }
}
