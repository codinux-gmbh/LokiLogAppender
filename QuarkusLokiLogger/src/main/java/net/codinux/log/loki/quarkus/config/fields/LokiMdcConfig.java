package net.codinux.log.loki.quarkus.config.fields;

import io.smallrye.config.WithDefault;
import net.codinux.log.quarkus.config.fields.MdcConfig;

public interface LokiMdcConfig extends MdcConfig {

    /**
     * If MDC (Mapped Diagnostic Context) should be included in Loki index.
     *
     * Defaults to {@code false} to avoid labels with high cardinality which is bad for Loki
     * (see e.g. https://grafana.com/docs/loki/latest/get-started/labels/bp-labels/ or
     * https://grafana.com/docs/loki/latest/get-started/labels/cardinality/).
     */
    @WithDefault(LokiFieldsConfig.IncludeMdcDefaultValueString)
    boolean include();

}