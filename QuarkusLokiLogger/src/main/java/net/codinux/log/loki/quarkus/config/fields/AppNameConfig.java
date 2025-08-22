package net.codinux.log.loki.quarkus.config.fields;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.fields.LogFieldsConfig;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;

@ConfigGroup
public interface AppNameConfig extends QuarkusFieldWithValueConfig {

    /**
     * If the app name field should be included as Label or StructuredMetadata in Loki.
     *
     * Defaults to {@code true}.
     *
     * The logged app name can be configured with {@link AppNameConfig#value()} ()}
     * and defaults to {@code ${quarkus.application.name}}.
     */
    @Override
    @WithDefault(LogFieldsConfig.AppNameDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the app name field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(LogFieldsConfig.AppNameDefaultFieldName)
    String fieldName();

    /**
     * The value of the application name field.
     *
     * Defaults to ${quarkus.application.name} (see <a href="https://quarkus.io/guides/all-config#quarkus-core_quarkus.application.name">Quarkus config quarkus.application.name</a>).
     */
    @Override
    @WithName("name")
    @WithDefault("${quarkus.application.name}")
    String value();

}