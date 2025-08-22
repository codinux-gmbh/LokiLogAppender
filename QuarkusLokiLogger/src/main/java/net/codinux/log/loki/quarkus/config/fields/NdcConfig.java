package net.codinux.log.loki.quarkus.config.fields;

import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.fields.LogFieldsConfig;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;

public interface NdcConfig extends QuarkusFieldConfig {

    /**
     * If NDC (Nested Diagnostic Context) should be included as Label or StructuredMetadata in Loki.
     */
    @Override
    @WithDefault(LogFieldsConfig.NdcDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the NDC field, defaults to "ndc".
     */
    @Override
    @WithName("fieldname")
    @WithDefault(LogFieldsConfig.NdcDefaultFieldName)
    String fieldName();

}