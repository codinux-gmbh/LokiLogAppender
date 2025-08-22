package net.codinux.log.loki.quarkus.config.fields;

import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.LogFieldsConfig;
import net.codinux.log.loki.config.field.IncludeField;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;

public interface LoggerClassNameConfig extends QuarkusFieldConfig {

    /**
     * In most cases the logger is a full qualified class name including the package names.
     * If Loki logger should try to extract the class name - that is without package name - of the logger and
     * include it as Label or StructuredMetadata in Loki.
     */
    @Override
    @WithDefault(LogFieldsConfig.LoggerClassNameDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * In most cases the logger is a full qualified class name including the package names.
     * Loki logger can try to extract class' name from full qualified logger and log this.
     * This is the field name for it.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(LogFieldsConfig.LoggerClassNameDefaultFieldName)
    String fieldName();

}