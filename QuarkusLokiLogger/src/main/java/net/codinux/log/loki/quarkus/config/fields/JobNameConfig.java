package net.codinux.log.loki.quarkus.config.fields;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.fields.LogFieldsConfig;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;

@ConfigGroup
public interface JobNameConfig extends QuarkusFieldWithValueConfig {

    /**
     * If the job name field should be included as Label or StructuredMetadata in Loki.
     *
     * Defaults to {@code true} to distinguish it from other log collectors.
     *
     * The logged job name can be configured with {@link JobNameConfig#value()} and defaults to {@code "LokiLogger"}.
     */
    @Override
    @WithDefault(LogFieldsConfig.JobNameDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the job name field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(LogFieldsConfig.JobNameDefaultFieldName)
    String fieldName();

    /**
     * The value of the job name tag. So that logs written with LokiLogger can be
     * differentiated from jobs of log collectors like fluentd and K8sLogCollector.
     *
     * Only gets written if {@link JobNameConfig#include()} is set to {@code true}.
     *
     * Defaults to {@code "LokiLogger"}.
     */
    @Override
    @WithName("name")
    @WithDefault(LogFieldsConfig.JobNameDefaultValue)
    String value();

}