package net.codinux.log.loki.quarkus.config.fields;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.config.fields.LogFieldsConfig;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;
import net.codinux.log.quarkus.converter.FieldNamePrefixConverter;

@ConfigGroup
public interface QuarkusPrefixFieldConfig {

    /**
     * If the field should be included as Label or StructuredMetadata in Loki.
     */
    @WithDefault(LogFieldsConfig.No)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The prefix of the Label or Structured Metadata.
     */
    @WithConverter(FieldNamePrefixConverter.class)
    String prefix();

}