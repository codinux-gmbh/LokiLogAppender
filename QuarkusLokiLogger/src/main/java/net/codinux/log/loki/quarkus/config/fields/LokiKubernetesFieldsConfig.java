package net.codinux.log.loki.quarkus.config.fields;

public class LokiKubernetesFieldsConfig {

    public static final String KubernetesFieldsPrefixDefaultValue = "";

    public static final boolean IncludePodNameDefaultValue = false;
    public static final String IncludePodNameDefaultValueString = "" + IncludePodNameDefaultValue;
    public static final String PodNameDefaultFieldName = "pod";

    public static final boolean IncludeContainerNameDefaultValue = false;
    public static final String IncludeContainerNameDefaultValueString = "" + IncludeContainerNameDefaultValue;
    public static final String ContainerNameDefaultFieldName = "container";

    public static final boolean IncludeImageNameDefaultValue = false;
    public static final String IncludeImageNameDefaultValueString = "" + IncludeImageNameDefaultValue;
    public static final String ImageNameDefaultFieldName = "image";

}