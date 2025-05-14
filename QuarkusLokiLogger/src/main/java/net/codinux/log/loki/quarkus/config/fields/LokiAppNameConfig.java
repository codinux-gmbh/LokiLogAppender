package net.codinux.log.loki.quarkus.config.fields;

import io.smallrye.config.WithDefault;
import net.codinux.log.quarkus.config.fields.AppNameConfig;

public interface LokiAppNameConfig extends AppNameConfig {

    /**
     * If the app name field should be included in Loki index.
     *
     * Defaults to {@code true}.
     *
     * The logged app name can be configured with {@link LokiAppNameConfig#appName()} ()}
     * and defaults to {@code ${quarkus.application.name}}.
     */
    @Override
    @WithDefault(LokiFieldsConfig.IncludeAppNameDefaultValueString)
    boolean include();

}