package net.codinux.log.loki.quarkus.config.fields.kubernetes;

import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.quarkus.config.fields.LokiKubernetesFieldsConfig;
import net.codinux.log.quarkus.config.fields.kubernetes.KubernetesInfoConfig;
import net.codinux.log.quarkus.converter.FieldNamePrefixConverter;

public interface LokiKubernetesInfoConfig extends KubernetesInfoConfig {

    /**
     * Sets a prefix for all Kubernetes info keys.
     *
     * Empty string or special value "off" turns prefix off.
     *
     * Defaults to an empty string (= no prefix). Others prefer "k8s".
     */
    @Override
    @WithDefault(LokiKubernetesFieldsConfig.KubernetesFieldsPrefixDefaultValue)
    @WithConverter(FieldNamePrefixConverter.class)
    String prefix();


    /**
     * Config for logged Kubernetes fields.
     */
    @Override
    @WithName("field")
    QuarkusLokiKubernetesFieldsConfig fields();

}