package net.codinux.log.loki.quarkus;

import net.codinux.log.LogAppenderFieldsConfig;
import net.codinux.log.config.quarkus.QuarkusLogAppenderFieldsConfig;
import net.codinux.log.loki.LokiJBossLoggingAppender;
import net.codinux.log.loki.LokiLogAppenderConfig;
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig;

public class QuarkusLokiLogAppender extends LokiJBossLoggingAppender {

    public QuarkusLokiLogAppender(QuarkusLokiLogAppenderConfig config) {
        super(mapConfig(config));
    }

    private static LokiLogAppenderConfig mapConfig(QuarkusLokiLogAppenderConfig config) {
        LokiLogAppenderConfig mappedConfig = new LokiLogAppenderConfig();
        LogAppenderFieldsConfig mappedFields = mappedConfig.getFields();
        QuarkusLogAppenderFieldsConfig fields = config.fields;

        mappedConfig.setEnabled(config.enable);
        mappedConfig.setHostUrl(config.hostUrl);
        mappedConfig.setUsername(config.username);
        mappedConfig.setPassword(config.password);
        mappedConfig.setTenantId(config.tenantId);

        mappedFields.setIncludeLogLevel(fields.logLevel.include);
        mappedFields.setLogLevelFieldName(fields.logLevel.fieldName);
        mappedFields.setIncludeLoggerName(fields.loggerName.include);
        mappedFields.setLoggerNameFieldName(fields.loggerName.fieldName);
        mappedFields.setIncludeLoggerClassName(fields.loggerClassName.include);
        mappedFields.setLoggerClassNameFieldName(fields.loggerClassName.fieldName);

        mappedFields.setIncludeThreadName(fields.threadName.include);
        mappedFields.setThreadNameFieldName(fields.threadName.fieldName);

        mappedFields.setIncludeAppName(fields.appName.include);
        mappedFields.setAppNameFieldName(fields.appName.fieldName);
        mappedFields.setAppName(fields.appName.appName);

        mappedFields.setIncludeHostName(fields.hostName.include);
        mappedFields.setHostNameFieldName(fields.hostName.fieldName);
        mappedFields.setIncludeHostIp(fields.hostIp.include);
        mappedFields.setHostIpFieldName(fields.hostIp.fieldName);

        mappedFields.setIncludeStacktrace(fields.stacktrace.include);
        mappedFields.setStacktraceFieldName(fields.stacktrace.fieldName);
        mappedFields.setStacktraceMaxFieldLength(fields.stacktrace.maxFieldLength);

        mappedFields.setIncludeMdc(fields.mdc.include);
        mappedFields.setMdcKeysPrefix(fields.mdc.prefix);
        mappedFields.setIncludeMarker(fields.marker.include);
        mappedFields.setMarkerFieldName(fields.marker.fieldName);
        mappedFields.setIncludeNdc(fields.ndc.include);
        mappedFields.setNdcFieldName(fields.ndc.fieldName);

        mappedFields.setIncludeKubernetesInfo(fields.kubernetesInfo.include);
        mappedFields.setKubernetesFieldsPrefix(fields.kubernetesInfo.prefix);
        mappedFields.setIncludeKubernetesLabels(fields.kubernetesInfo.labels.include);
        mappedFields.setKubernetesLabelsPrefix(fields.kubernetesInfo.labels.prefix);
        mappedFields.setIncludeKubernetesAnnotations(fields.kubernetesInfo.annotations.include);
        mappedFields.setKubernetesAnnotationsPrefix(fields.kubernetesInfo.annotations.prefix);

        mappedConfig.setMaxBufferedLogRecords(config.maxBufferedLogRecords);
        mappedConfig.setMaxLogRecordsPerBatch(config.maxLogRecordsPerBatch);
        mappedConfig.setSendLogRecordsPeriodMillis(config.sendLogRecordsPeriodMillis);

        return mappedConfig;
    }
}
