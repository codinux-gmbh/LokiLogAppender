package net.codinux.log.loki.quarkus.config.fields;

public class LokiFieldsConfig {

    public static final boolean IncludeAppNameDefaultValue = true;
    public static final String IncludeAppNameDefaultValueString = "" + IncludeAppNameDefaultValue;

    public static final boolean IncludeJobNameDefaultValue = true;
    public static final String IncludeJobNameDefaultValueString = "" + IncludeJobNameDefaultValue;
    public static final String JobNameDefaultValue = "LokiLogger";

    public static final boolean IncludeMdcDefaultValue = false;
    public static final String IncludeMdcDefaultValueString = "" + IncludeMdcDefaultValue;

}