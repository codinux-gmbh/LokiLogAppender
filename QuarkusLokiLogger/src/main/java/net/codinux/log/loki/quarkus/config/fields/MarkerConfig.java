package net.codinux.log.loki.quarkus.config.fields;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.fields.LogFieldsConfig;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;

@ConfigGroup
public interface MarkerConfig extends QuarkusFieldConfig {

    /**
     * If Marker should be included as Label or StructuredMetadata in Loki.
     */
    @Override
    @WithDefault(LogFieldsConfig.MarkerDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the Marker field, defaults to "marker".
     */
    @Override
    @WithName("fieldname")
    @WithDefault(LogFieldsConfig.MarkerDefaultFieldName)
    String fieldName();

}