package net.codinux.log.loki.quarkus.config.fields.kubernetes;

import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.quarkus.config.fields.LokiKubernetesFieldsConfig;
import net.codinux.log.quarkus.config.fields.kubernetes.ImageNameConfig;

public interface LokiImageNameConfig extends ImageNameConfig {

    /**
     * If the image name should be included in index.
     *
     * Defaults to {@code false} to avoid labels with high cardinality which is bad for Loki.
     */
    @WithDefault(LokiKubernetesFieldsConfig.IncludeImageNameDefaultValueString)
    boolean include();

    /**
     * The name of the image name index field.
     */
    @WithName("fieldname")
    @WithDefault(LokiKubernetesFieldsConfig.ImageNameDefaultFieldName)
    String fieldName();

}