package net.codinux.log.loki.quarkus.config.fields.kubernetes;

import io.smallrye.config.WithName;
import net.codinux.log.quarkus.config.fields.kubernetes.QuarkusKubernetesFieldsConfig;

public interface QuarkusLokiKubernetesFieldsConfig extends QuarkusKubernetesFieldsConfig {

    /**
     * Config for the Pod name index field.
     */
    @Override
    @WithName("podname")
    LokiPodNameConfig podName();

    /**
     * Config for the container name index field.
     */
    @Override
    @WithName("containername")
    LokiContainerNameConfig containerName();

    /**
     * Config for the image name index field.
     */
    @Override
    @WithName("imagename")
    LokiImageNameConfig imageName();

}