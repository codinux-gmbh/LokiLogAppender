package net.codinux.log.loki

import net.codinux.log.LogRecord
import net.codinux.log.LogRecordMapper
import net.codinux.log.config.LogAppenderFieldsConfig
import net.codinux.log.loki.model.LogStream
import net.codinux.log.loki.util.LokiLabelEscaper

open class LokiLogRecordMapper(config: LogAppenderFieldsConfig) : LogRecordMapper(config, false, true) {

    protected open val labelEscaper = LokiLabelEscaper.Default

    protected open val logsDynamicStructuredMetadata = logsDynamicStructuredMetadata(config)


    override fun escapeDynamicLabelName(key: String) =
        labelEscaper.escapeLabelName(key)


    open fun mapStaticLabels(labels: MutableMap<String, String?>) {
        // TODO: for now we hard code which fields get logged as labels and which as structured metadata
        mapField(labels, config.includeAppName, config.appNameFieldName, config.appName)
        mapField(labels, config.includeJobName, config.jobNameFieldName, config.jobName)
    }

    open fun <T> mapDynamicLabels(record: LogRecord<T>, labels: MutableMap<String, String?>) {
        // TODO: for now we hard code which fields get logged as labels and which as structured metadata
        mapField(labels, config.includeLogLevel, config.logLevelFieldName, record.level)
    }


    open fun mapStaticStructuredMetadata(structuredMetadata: MutableMap<String, String?>) {
        // TODO: for now we hard code which fields get logged as labels and which as structured metadata
        mapField(structuredMetadata, config.includeHostName, config.hostNameFieldName, processData.hostName)
        mapField(structuredMetadata, config.includeAppVersion, config.appVersionFieldName, config.appVersion)

        mapPodInfoFields(structuredMetadata)
    }

    open fun mapDynamicStructuredMetadata(record: LogRecord<LogStream>, structuredMetadata: MutableMap<String, String?>) {
        if (logsDynamicStructuredMetadata) {
            // TODO: for now we hard code which fields get logged as labels and which as structured metadata
            mapField(structuredMetadata, config.includeLoggerName, config.loggerNameFieldName, record.loggerName)
            mapField(structuredMetadata, config.includeLoggerClassName, config.loggerClassNameFieldName) { record.loggerName?.let { extractLoggerClassName(it) } }

            mapField(structuredMetadata, config.includeStacktrace, config.stacktraceFieldName) { getStacktrace(record.exception) }

            mapMdcFields(record, structuredMetadata, config.includeMdc && record.mdc != null, record.mdc)
            mapDynamicFieldIfNotNull(structuredMetadata, config.includeMarker, config.markerFieldName, record.marker)
            mapDynamicFieldIfNotNull(structuredMetadata, config.includeNdc, config.ndcFieldName, record.ndc)
        }
    }

    protected fun logsDynamicStructuredMetadata(config: LogAppenderFieldsConfig): Boolean =
        config.includeLoggerName || config.includeLoggerClassName
                || config.includeThreadName
                || config.includeStacktrace
                || config.includeMdc || config.includeMarker || config.includeNdc

}