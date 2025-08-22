package net.codinux.log.loki.quarkus.config.fields;

import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.fields.LogFieldsConfig;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;

public interface QuarkusFieldConfig {

    /**
     * If the field should be included as Label or StructuredMetadata in Loki.
     */
    @WithDefault(LogFieldsConfig.No)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the Label or Structured Metadata.
     */
    @WithName("fieldname")
    String fieldName();

}