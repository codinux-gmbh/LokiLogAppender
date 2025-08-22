package net.codinux.log.loki.quarkus.config.fields;

import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.fields.LogFieldsConfig;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;

public interface HostNameConfig extends QuarkusFieldConfig {

    /**
     * If the host name field should be included as Label or StructuredMetadata in Loki.
     */
    @Override
    @WithDefault(LogFieldsConfig.HostNameDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the host name field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(LogFieldsConfig.HostNameDefaultFieldName)
    String fieldName();

}