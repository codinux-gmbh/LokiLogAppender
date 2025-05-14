package net.codinux.log.loki.quarkus.config.fields.kubernetes;

import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.quarkus.config.fields.LokiKubernetesFieldsConfig;
import net.codinux.log.quarkus.config.fields.kubernetes.ContainerNameConfig;

public interface LokiContainerNameConfig extends ContainerNameConfig {

    /**
     * If the container name should be included in index.
     *
     * Defaults to {@code false}.
     */
    @Override
    @WithDefault(LokiKubernetesFieldsConfig.IncludeContainerNameDefaultValueString)
    boolean include();

    /**
     * The name of the container name index field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(LokiKubernetesFieldsConfig.ContainerNameDefaultFieldName)
    String fieldName();

}