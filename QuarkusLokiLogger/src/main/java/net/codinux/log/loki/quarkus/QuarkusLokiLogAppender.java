package net.codinux.log.loki.quarkus;

import net.codinux.log.LogAppenderFieldsConfig;
import net.codinux.log.config.KubernetesFieldsConfig;
import net.codinux.log.config.quarkus.QuarkusLogAppenderFieldsConfig;
import net.codinux.log.config.quarkus.fields.kubernetes.QuarkusKubernetesFieldsConfig;
import net.codinux.log.loki.LokiJBossLoggingAppender;
import net.codinux.log.loki.LokiLogAppenderConfig;
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig;

public class QuarkusLokiLogAppender extends LokiJBossLoggingAppender {

    public QuarkusLokiLogAppender(QuarkusLokiLogAppenderConfig config) {
        super(mapConfig(config));
    }

    private static LokiLogAppenderConfig mapConfig(QuarkusLokiLogAppenderConfig config) {
        LokiLogAppenderConfig mappedConfig = new LokiLogAppenderConfig();

        mappedConfig.setEnabled(config.enable);
        mappedConfig.setHostUrl(config.hostUrl);
        mappedConfig.setUsername(config.username);
        mappedConfig.setPassword(config.password);
        mappedConfig.setTenantId(config.tenantId);

        mappedConfig.setMaxBufferedLogRecords(config.maxBufferedLogRecords);
        mappedConfig.setMaxLogRecordsPerBatch(config.maxLogRecordsPerBatch);
        mappedConfig.setSendLogRecordsPeriodMillis(config.sendLogRecordsPeriodMillis);

        mappedConfig.setFields(mapFields(config.fields));

        return mappedConfig;
    }

    private static LogAppenderFieldsConfig mapFields(QuarkusLogAppenderFieldsConfig fields) {
        LogAppenderFieldsConfig mappedFields = new LogAppenderFieldsConfig();

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

        mappedFields.setIncludeAppVersion(fields.appVersion.include);
        mappedFields.setAppVersionFieldName(fields.appVersion.fieldName);
        mappedFields.setAppVersion(fields.appVersion.appVersion);

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
        mappedFields.setKubernetesFields(mapKubernetesFields(fields.kubernetesInfo.fields));

        return mappedFields;
    }

    private static KubernetesFieldsConfig mapKubernetesFields(QuarkusKubernetesFieldsConfig fields) {
        KubernetesFieldsConfig mappedFields = new KubernetesFieldsConfig();

        mappedFields.setIncludeNamespace(fields.namespace.include);
        mappedFields.setNamespaceFieldName(fields.namespace.fieldName);

        mappedFields.setIncludePodName(fields.podName.include);
        mappedFields.setPodNameFieldName(fields.podName.fieldName);

        mappedFields.setIncludeContainerName(fields.containerName.include);
        mappedFields.setContainerNameFieldName(fields.containerName.fieldName);

        mappedFields.setIncludeImageName(fields.imageName.include);
        mappedFields.setImageNameFieldName(fields.imageName.fieldName);

        mappedFields.setIncludeNodeName(fields.nodeName.include);
        mappedFields.setNodeNameFieldName(fields.nodeName.fieldName);

        mappedFields.setIncludeNodeIp(fields.nodeIp.include);
        mappedFields.setNodeIpFieldName(fields.nodeIp.fieldName);

        mappedFields.setIncludePodIp(fields.podIp.include);
        mappedFields.setPodIpFieldName(fields.podIp.fieldName);

        mappedFields.setIncludeStartTime(fields.startTime.include);
        mappedFields.setStartTimeFieldName(fields.startTime.fieldName);

        mappedFields.setIncludeRestartCount(fields.restartCount.include);
        mappedFields.setRestartCountFieldName(fields.restartCount.fieldName);

        mappedFields.setIncludePodUid(fields.podUid.include);
        mappedFields.setPodUidFieldName(fields.podUid.fieldName);

        mappedFields.setIncludeContainerId(fields.containerId.include);
        mappedFields.setContainerIdFieldName(fields.containerId.fieldName);

        mappedFields.setIncludeImageId(fields.imageName.include);
        mappedFields.setImageIdFieldName(fields.imageName.fieldName);

        mappedFields.setIncludeLabels(fields.labels.include);
        mappedFields.setLabelsPrefix(fields.labels.prefix);

        mappedFields.setIncludeAnnotations(fields.annotations.include);
        mappedFields.setAnnotationsPrefix(fields.annotations.prefix);

        return mappedFields;
    }
}
