package net.codinux.log.loki.quarkus.config.fields;

public interface QuarkusFieldWithValueConfig extends QuarkusFieldConfig {

    /**
     * The value of the field.
     */
    String value();

}