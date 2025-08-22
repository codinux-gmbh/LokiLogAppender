package net.codinux.log.loki.quarkus.config.fields;

import io.quarkus.runtime.annotations.ConfigGroup;

@ConfigGroup
public interface QuarkusFieldWithValueConfig extends QuarkusFieldConfig {

    /**
     * The value of the field.
     */
    String value();

}