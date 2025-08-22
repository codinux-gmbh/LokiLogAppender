package net.codinux.log.loki.quarkus.config.fields;

import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.LogFieldsConfig;
import net.codinux.log.loki.config.field.IncludeField;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;

public interface LoggerNameConfig extends QuarkusFieldConfig {

    /**
     * If the full qualified name of the logger should be included as Label or StructuredMetadata in Loki.
     */
    @Override
    @WithDefault(LogFieldsConfig.LoggerNameDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the logger field (that is the full qualified logger name which includes in most cases the package names).
     */
    @Override
    @WithName("fieldname")
    @WithDefault(LogFieldsConfig.LoggerNameDefaultFieldName)
    String fieldName();

}