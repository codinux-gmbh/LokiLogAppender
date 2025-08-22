package net.codinux.log.loki.quarkus.config.fields;

import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.LogFieldsConfig;
import net.codinux.log.loki.config.field.IncludeField;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;

public interface ThreadNameConfig extends QuarkusFieldConfig {

    /**
     * If the thread name field should be included as Label or StructuredMetadata in Loki.
     */
    @Override
    @WithDefault(LogFieldsConfig.ThreadNameDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the thread name field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(LogFieldsConfig.ThreadNameDefaultFieldName)
    String fieldName();

}