package net.codinux.log.loki.quarkus;

import net.codinux.log.LogAppenderConfig;
import net.codinux.log.loki.LokiJBossLoggingAppender;
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig;

public class QuarkusLokiLogAppender extends LokiJBossLoggingAppender {

    public QuarkusLokiLogAppender(QuarkusLokiLogAppenderConfig config) {
        super(mapConfig(config));
    }

    private static LogAppenderConfig mapConfig(QuarkusLokiLogAppenderConfig config) {
        LogAppenderConfig mappedConfig = new LogAppenderConfig();

        mappedConfig.setEnabled(config.enable);
        mappedConfig.setHost(config.endpointHost);

        mappedConfig.setIncludeLogLevel(config.logLevel.include);
        mappedConfig.setLogLevelFieldName(config.logLevel.fieldName);
        mappedConfig.setIncludeLoggerName(config.loggerName.include);
        mappedConfig.setLoggerNameFieldName(config.loggerName.fieldName);
        mappedConfig.setIncludeLoggerClassName(config.loggerClassName.include);
        mappedConfig.setLoggerClassNameFieldName(config.loggerClassName.fieldName);

        mappedConfig.setIncludeThreadName(config.threadName.include);
        mappedConfig.setThreadNameFieldName(config.threadName.fieldName);

        mappedConfig.setIncludeHostName(config.hostName.include);
        mappedConfig.setHostNameFieldName(config.hostName.fieldName);
        mappedConfig.setIncludeHostIp(config.hostIp.include);
        mappedConfig.setHostIpFieldName(config.hostIp.fieldName);

        mappedConfig.setIncludeAppName(config.appName.include);
        mappedConfig.setAppNameFieldName(config.appName.fieldName);
        mappedConfig.setAppName(config.appName.appName);

        mappedConfig.setIncludeStacktrace(config.stacktrace.include);
        mappedConfig.setStacktraceFieldName(config.stacktrace.fieldName);
        mappedConfig.setStacktraceMaxFieldLength(config.stacktrace.maxFieldLength);

        mappedConfig.setIncludeMdc(config.mdc.include);
        mappedConfig.setMdcKeysPrefix(config.mdc.prefix);
        mappedConfig.setIncludeMarker(config.marker.include);
        mappedConfig.setMarkerFieldName(config.marker.fieldName);
        mappedConfig.setIncludeNdc(config.ndc.include);
        mappedConfig.setNdcFieldName(config.ndc.fieldName);

        mappedConfig.setIncludeKubernetesInfo(config.kubernetesInfo.include);
        mappedConfig.setKubernetesFieldsPrefix(config.kubernetesInfo.prefix);
        mappedConfig.setIncludeKubernetesLabels(config.kubernetesInfo.labels.include);
        mappedConfig.setKubernetesLabelsPrefix(config.kubernetesInfo.labels.prefix);
        mappedConfig.setIncludeKubernetesAnnotations(config.kubernetesInfo.annotations.include);
        mappedConfig.setKubernetesAnnotationsPrefix(config.kubernetesInfo.annotations.prefix);

        mappedConfig.setAppendLogsAsync(config.appendLogsAsync);

        mappedConfig.setMaxLogRecordsPerBatch(config.maxLogRecordsPerBatch);
        mappedConfig.setMaxBufferedLogRecords(config.maxBufferedLogRecords);
        mappedConfig.setSendLogRecordsPeriodMillis(config.sendLogRecordsPeriodMillis);

        return mappedConfig;
    }
}
