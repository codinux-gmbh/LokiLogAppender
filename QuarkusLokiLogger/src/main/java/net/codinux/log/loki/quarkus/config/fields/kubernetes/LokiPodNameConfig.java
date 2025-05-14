package net.codinux.log.loki.quarkus.config.fields.kubernetes;

import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.quarkus.config.fields.LokiKubernetesFieldsConfig;
import net.codinux.log.quarkus.config.fields.kubernetes.PodNameConfig;

public interface LokiPodNameConfig extends PodNameConfig {

    /**
     * If the Pod name should be included in index.
     *
     * Defaults to {@code false} to avoid labels with high cardinality which is bad for Loki.
     */
    @Override
    @WithDefault(LokiKubernetesFieldsConfig.IncludePodNameDefaultValueString)
    boolean include();

    /**
     * The name of the Pod name index field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(LokiKubernetesFieldsConfig.PodNameDefaultFieldName)
    String fieldName();

}