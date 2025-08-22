package net.codinux.log.loki.quarkus.config.fields;

import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.LogFieldsConfig;
import net.codinux.log.loki.config.field.IncludeField;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;

public interface AppVersionConfig extends QuarkusFieldWithValueConfig {

    /**
     * If the app version field should be included as Label or StructuredMetadata in Loki.
     */
    @Override
    @WithDefault(LogFieldsConfig.AppVersionDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the app version field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(LogFieldsConfig.AppVersionDefaultFieldName)
    String fieldName();

    /**
     * The application version as it gets sent to logging backend.
     *
     * Defaults to ${quarkus.application.version} (see <a href="https://quarkus.io/guides/all-config#quarkus-core_quarkus.application.version">Quarkus config quarkus.application.version</a>).
     */
    @Override
    @WithName("version")
    @WithDefault("${quarkus.application.version}")
    String value();

}