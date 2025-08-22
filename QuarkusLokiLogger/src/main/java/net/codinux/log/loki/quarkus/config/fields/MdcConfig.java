package net.codinux.log.loki.quarkus.config.fields;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import net.codinux.log.loki.config.fields.LogFieldsConfig;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;
import net.codinux.log.quarkus.converter.FieldNamePrefixConverter;

@ConfigGroup
public interface MdcConfig extends QuarkusPrefixFieldConfig {

    /**
     * If MDC (Mapped Diagnostic Context) should be included as Label or StructuredMetadata in Loki.
     *
     * Defaults to {@code StructuredMetadata} to avoid labels with high cardinality which is bad for Loki
     * (see e.g. https://grafana.com/docs/loki/latest/get-started/labels/bp-labels/ or
     * https://grafana.com/docs/loki/latest/get-started/labels/cardinality/).
     */
    @Override
    @WithDefault(LogFieldsConfig.MdcDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * Sets a prefix for all MDC keys.
     *
     * Empty string or special value "off" turns prefix off.
     *
     * E.g. prefix is set to "mdc", then the MDC gets stored as:
     *  mdc.key_1=value_1
     *  mdc.key_2=value_2
     *
     * instead of:
     *  key_1=value_1
     *  key_2=value_2
     *
     *  Defaults to {@code mdc}.
     */
    @Override
    @WithDefault(LogFieldsConfig.MdcDefaultPrefixValue)
    @WithConverter(FieldNamePrefixConverter.class)
    String prefix();

}