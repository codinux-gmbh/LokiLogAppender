package net.codinux.log.loki.quarkus.config.mapper;

import net.codinux.log.loki.config.LokiLogAppenderConfig;
import net.codinux.log.loki.config.fields.*;
import net.codinux.log.loki.config.fields.kubernetes.KubernetesFieldsConfig;
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig;
import net.codinux.log.loki.quarkus.config.fields.QuarkusFieldConfig;
import net.codinux.log.loki.quarkus.config.fields.QuarkusFieldWithValueConfig;
import net.codinux.log.loki.quarkus.config.fields.QuarkusLogFieldsConfig;
import net.codinux.log.loki.quarkus.config.fields.StacktraceConfig;
import net.codinux.log.loki.quarkus.config.fields.kubernetes.QuarkusKubernetesFieldsConfig;
import net.codinux.log.quarkus.config.mapper.QuarkusConfigMapper;

import static net.codinux.log.quarkus.config.mapper.QuarkusConfigMapper.mapNullableString;

public class QuarkusLokiConfigMapper {

    public static LokiLogAppenderConfig mapConfig(QuarkusLokiLogAppenderConfig config) {
        return new LokiLogAppenderConfig(
                config.enable(),
                config.lokiBaseUrl(),
                mapNullableString(config.authentication().username()),
                mapNullableString(config.authentication().password()),
                mapNullableString(config.tenantId()),
                mapFields(config.fields()),
                QuarkusConfigMapper.mapWriterConfig(config.writer()),
                mapNullableString(config.stateLoggerName())
        );
    }

    private static LogFieldsConfig mapFields(QuarkusLogFieldsConfig fields) {
        return new LogFieldsConfig(
                map(fields.logLevel()),
                map(fields.loggerName()),
                map(fields.loggerClassName()),

                map(fields.threadName()),
                mapStacktraceConfig(fields.stacktrace()),

                map(fields.appName()),
                map(fields.appVersion()),
                map(fields.jobName()),

                map(fields.hostName()),
                map(fields.hostIp()),

                new PrefixFieldConfig(fields.mdc().prefix(), fields.mdc().include()),
                map(fields.marker()),
                map(fields.ndc()),

                mapKubernetesFields(fields.kubernetes())
        );
    }

    private static KubernetesFieldsConfig mapKubernetesFields(QuarkusKubernetesFieldsConfig kubernetes) {
        return new KubernetesFieldsConfig(
                kubernetes.include(),
                kubernetes.prefix(),

                map(kubernetes.namespace()),

                map(kubernetes.podName()),
                map(kubernetes.podIp()),
                map(kubernetes.podUid()),

                map(kubernetes.containerName()),

                map(kubernetes.imageName()),

                map(kubernetes.nodeName()),
                map(kubernetes.nodeIp())
        );
    }

    private static FieldConfig map(QuarkusFieldConfig config) {
        return new FieldConfig(config.fieldName(), config.include());
    }

    private static FieldWithValueConfig map(QuarkusFieldWithValueConfig config) {
        return new FieldWithValueConfig(config.fieldName(), config.include(), config.value());
    }

    private static StacktraceFieldConfig mapStacktraceConfig(StacktraceConfig config) {
        return new StacktraceFieldConfig(config.fieldName(), config.include(), config.maxFieldLength());
    }

}