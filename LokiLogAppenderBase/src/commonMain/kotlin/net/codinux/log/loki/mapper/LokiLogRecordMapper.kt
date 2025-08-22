package net.codinux.log.loki.mapper

import net.codinux.log.mapper.FieldMapper
import net.codinux.log.LogRecord
import net.codinux.log.data.ProcessData
import net.codinux.log.kubernetes.PodInfo
import net.codinux.log.loki.config.fields.LogFieldsConfig
import net.codinux.log.loki.config.LokiLogAppenderConfig
import net.codinux.log.loki.model.LogStream

open class LokiLogRecordMapper(
    protected open val config: LokiLogAppenderConfig,
    protected open val fieldMapper: FieldMapper = FieldMapper(false, fieldEscaper = LokiLabelEscaper.Default),
) {

    protected open val fields = config.fields

    protected open val logsDynamicStructuredMetadata = logsDynamicStructuredMetadata(config.fields)


    open fun mapStaticLabels(labels: MutableMap<String, String?>, processData: ProcessData, podInfo: PodInfo?) {
        mapField(labels, fields.appName.logAsLabel, fields.appName.name, fields.appName.value)
        mapField(labels, fields.appVersion.logAsLabel, fields.appVersion.name, fields.appVersion.value)
        mapField(labels, fields.job.logAsLabel, fields.job.name, fields.job.value)
        mapField(labels, fields.hostName.logAsLabel, fields.hostName.name, processData.hostName)
        mapField(labels, fields.hostIp.logAsLabel, fields.hostIp.name, processData.hostIp)

        // TODO
//        fieldMapper.mapPodInfoFields(labels, fields.includeKubernetesInfo, podInfo, fields.kubernetesFieldsPrefix, fields.kubernetesFields)
    }

    open fun <T> mapDynamicLabels(record: LogRecord<T>, labels: MutableMap<String, String?>) {
        mapField(labels, fields.logLevel.logAsLabel, fields.logLevel.name, record.level)
        mapField(labels, fields.logger.logAsLabel, fields.logger.name, record.loggerName)
        mapField(labels, fields.loggerClass.logAsLabel, fields.loggerClass.name) { record.loggerName?.let { fieldMapper.extractLoggerClassName(it) } }
        mapField(labels, fields.thread.logAsLabel, fields.thread.name, record.threadName)

        mapField(labels, fields.stacktrace.logAsLabel, fields.stacktrace.name) { getStacktrace(record.exception) }

        fieldMapper.mapMdcFields(record, labels, fields.mdc.logAsLabel && record.mdc != null, record.mdc, fields.mdc.prefix)
        mapDynamicFieldIfNotNull(labels, fields.marker.logAsLabel, fields.marker.name, record.marker)
        mapDynamicFieldIfNotNull(labels, fields.ndc.logAsLabel, fields.ndc.name, record.ndc)
    }


    open fun mapStaticStructuredMetadata(structuredMetadata: MutableMap<String, String?>, processData: ProcessData, podInfo: PodInfo?) {
        mapField(structuredMetadata, fields.appName.logAsStructuredMetadata, fields.appName.name, fields.appName.value)
        mapField(structuredMetadata, fields.appVersion.logAsStructuredMetadata, fields.appVersion.name, fields.appVersion.value)
        mapField(structuredMetadata, fields.job.logAsStructuredMetadata, fields.job.name, fields.job.value)
        mapField(structuredMetadata, fields.hostName.logAsStructuredMetadata, fields.hostName.name, processData.hostName)
        mapField(structuredMetadata, fields.hostIp.logAsStructuredMetadata, fields.hostIp.name, processData.hostIp)

        fieldMapper.mapPodInfoFields(structuredMetadata, fields.includeKubernetesInfo, podInfo, fields.kubernetesFieldsPrefix, fields.kubernetesFields)
    }

    open fun mapDynamicStructuredMetadata(record: LogRecord<LogStream>, structuredMetadata: MutableMap<String, String?>) {
        if (logsDynamicStructuredMetadata) {
            mapField(structuredMetadata, fields.logLevel.logAsStructuredMetadata, fields.logLevel.name, record.level)
            mapField(structuredMetadata, fields.logger.logAsStructuredMetadata, fields.logger.name, record.loggerName)
            mapField(structuredMetadata, fields.loggerClass.logAsStructuredMetadata, fields.loggerClass.name) { record.loggerName?.let { fieldMapper.extractLoggerClassName(it) } }
            mapField(structuredMetadata, fields.thread.logAsStructuredMetadata, fields.thread.name, record.threadName)

            mapField(structuredMetadata, fields.stacktrace.logAsStructuredMetadata, fields.stacktrace.name) { getStacktrace(record.exception) }

            fieldMapper.mapMdcFields(record, structuredMetadata, fields.mdc.logAsStructuredMetadata && record.mdc != null, record.mdc, fields.mdc.prefix)
            mapDynamicFieldIfNotNull(structuredMetadata, fields.marker.logAsStructuredMetadata, fields.marker.name, record.marker)
            mapDynamicFieldIfNotNull(structuredMetadata, fields.ndc.logAsStructuredMetadata, fields.ndc.name, record.ndc)
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
        fields.logLevel.logAsStructuredMetadata || fields.logger.logAsStructuredMetadata || fields.loggerClass.logAsStructuredMetadata
                || fields.thread.logAsStructuredMetadata
                || fields.stacktrace.logAsStructuredMetadata
                || fields.mdc.logAsStructuredMetadata || fields.marker.logAsStructuredMetadata || fields.ndc.logAsStructuredMetadata

}