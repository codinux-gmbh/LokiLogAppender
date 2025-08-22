package net.codinux.log.loki.quarkus.config.mapper;

import net.codinux.log.loki.config.fields.LogFieldsConfig;
import net.codinux.log.loki.config.LokiLogAppenderConfig;
import net.codinux.log.loki.config.fields.FieldConfig;
import net.codinux.log.loki.config.fields.FieldWithValueConfig;
import net.codinux.log.loki.config.fields.PrefixFieldConfig;
import net.codinux.log.loki.config.fields.StacktraceFieldConfig;
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig;
import net.codinux.log.loki.quarkus.config.fields.QuarkusFieldConfig;
import net.codinux.log.loki.quarkus.config.fields.QuarkusFieldWithValueConfig;
import net.codinux.log.loki.quarkus.config.fields.QuarkusLokiLogAppenderFieldsConfig;
import net.codinux.log.loki.quarkus.config.fields.StacktraceConfig;
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

    private static LogFieldsConfig mapFields(QuarkusLokiLogAppenderFieldsConfig fields) {
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

                // TODO
                fields.kubernetesInfo().include(),
                fields.kubernetesInfo().prefix(),
                QuarkusConfigMapper.mapKubernetesFields(fields.kubernetesInfo().fields())
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