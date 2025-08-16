package net.codinux.log.loki

import net.codinux.log.FieldMapper
import net.codinux.log.LogRecord
import net.codinux.log.data.ProcessData
import net.codinux.log.kubernetes.PodInfo
import net.codinux.log.loki.config.LogFieldsConfig
import net.codinux.log.loki.config.LokiLogAppenderConfig
import net.codinux.log.loki.model.LogStream
import net.codinux.log.loki.util.LokiLabelEscaper

open class LokiLogRecordMapper(
    protected open val config: LokiLogAppenderConfig,
    protected open val fieldMapper: FieldMapper = FieldMapper(false, fieldEscaper = LokiLabelEscaper.Default),
) {

    protected open val fields = config.fields

    protected open val logsDynamicStructuredMetadata = logsDynamicStructuredMetadata(config.fields)


    open fun mapStaticLabels(labels: MutableMap<String, String?>) {
        // TODO: for now we hard code which fields get logged as labels and which as structured metadata
        mapField(labels, fields.appName.isIncluded, fields.appName.name, fields.appName.value)
        mapField(labels, fields.job.isIncluded, fields.job.name, fields.job.value)
    }

    open fun <T> mapDynamicLabels(record: LogRecord<T>, labels: MutableMap<String, String?>) {
        // TODO: for now we hard code which fields get logged as labels and which as structured metadata
        mapField(labels, fields.logLevel.isIncluded, fields.logLevel.name, record.level)
    }


    open fun mapStaticStructuredMetadata(structuredMetadata: MutableMap<String, String?>, processData: ProcessData, podInfo: PodInfo?) {
        // TODO: for now we hard code which fields get logged as labels and which as structured metadata
        mapField(structuredMetadata, fields.hostName.isIncluded, fields.hostName.name, processData.hostName)
        mapField(structuredMetadata, fields.appVersion.isIncluded, fields.appVersion.name, fields.appVersion.value)

        fieldMapper.mapPodInfoFields(structuredMetadata, fields.includeKubernetesInfo, podInfo, fields.kubernetesFieldsPrefix, fields.kubernetesFields)
    }

    open fun mapDynamicStructuredMetadata(record: LogRecord<LogStream>, structuredMetadata: MutableMap<String, String?>) {
        if (logsDynamicStructuredMetadata) {
            // TODO: for now we hard code which fields get logged as labels and which as structured metadata
            mapField(structuredMetadata, fields.logger.isIncluded, fields.logger.name, record.loggerName)
            mapField(structuredMetadata, fields.loggerClass.isIncluded, fields.loggerClass.name) { record.loggerName?.let { fieldMapper.extractLoggerClassName(it) } }

            mapField(structuredMetadata, fields.stacktrace.isIncluded, fields.stacktrace.name) { getStacktrace(record.exception) }

            fieldMapper.mapMdcFields(record, structuredMetadata, fields.mdc.isIncluded && record.mdc != null, record.mdc, fields.mdc.prefix)
            mapDynamicFieldIfNotNull(structuredMetadata, fields.marker.isIncluded, fields.marker.name, record.marker)
            mapDynamicFieldIfNotNull(structuredMetadata, fields.ndc.isIncluded, fields.ndc.name, record.ndc)
        }
    }


    protected open fun mapField(fields: MutableMap<String, String?>, includeField: Boolean, fieldName: String, valueSupplier: () -> String?) =
        fieldMapper.mapField(fields, includeField, fieldName, valueSupplier)

    protected open fun mapField(fields: MutableMap<String, String?>, includeField: Boolean, fieldName: String, value: String?) =
        fieldMapper.mapField(fields, includeField, fieldName, value)

    protected open fun mapDynamicFieldIfNotNull(fields: MutableMap<String, String?>, includeField: Boolean, fieldName: String, value: String?) =
        fieldMapper.mapDynamicFieldIfNotNull(fields, includeField, fieldName, value)


    open fun getStacktrace(exception: Throwable?): String? =
        fieldMapper.getStacktrace(exception, fields.stacktrace.isIncluded, fields.stacktrace.maxFieldLength)

    open fun escapeControlCharacters(value: String): String =
    // we have to escape single backslashes as Loki doesn't accept control characters
        // (returns then 400 Bad Request invalid control character found: 10, error found in #10 byte of ...)
        value.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t")
            .replace("\"", "\\\"")


    protected fun logsDynamicStructuredMetadata(fields: LogFieldsConfig): Boolean =
        fields.logger.isIncluded || fields.loggerClass.isIncluded
                || fields.thread.isIncluded
                || fields.stacktrace.isIncluded
                || fields.mdc.isIncluded || fields.marker.isIncluded || fields.ndc.isIncluded

}