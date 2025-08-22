package net.codinux.log.loki.quarkus.config.fields;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.fields.LogFieldsConfig;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;

@ConfigGroup
public interface StacktraceConfig extends QuarkusFieldConfig {

    /**
     * If the stacktrace field should be included as Label or StructuredMetadata in Loki.
     */
    @Override
    @WithDefault(LogFieldsConfig.StacktraceDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the stacktrace field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(LogFieldsConfig.StacktraceDefaultFieldName)
    String fieldName();

    /**
     * The name of the stacktrace field.
     */
    @WithName("maxFieldLength")
    @WithDefault(LogFieldsConfig.StacktraceMaxFieldLengthDefaultValueString)
    int maxFieldLength();

}