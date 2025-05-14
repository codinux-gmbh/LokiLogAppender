package net.codinux.log.loki.quarkus.config.fields.kubernetes;

import io.smallrye.config.WithName;
import net.codinux.log.quarkus.config.fields.kubernetes.KubernetesInfoConfig;

public interface LokiKubernetesInfoConfig extends KubernetesInfoConfig {


    /**
     * Config for logged Kubernetes fields.
     */
    @Override
    @WithName("field")
    QuarkusLokiKubernetesFieldsConfig fields();

}