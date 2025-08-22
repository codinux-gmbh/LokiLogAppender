package net.codinux.log.loki.quarkus.config.fields;

import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.fields.LogFieldsConfig;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;

public interface HostIpConfig extends QuarkusFieldConfig {

    /**
     * If the host IP field should be included as Label or StructuredMetadata in Loki.
     */
    @Override
    @WithDefault(LogFieldsConfig.HostIpDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the host IP field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(LogFieldsConfig.HostIpDefaultFieldName)
    String fieldName();

}