package net.codinux.log.loki.quarkus.config.fields.kubernetes;

import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.config.fields.kubernetes.KubernetesFieldsConfig;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;
import net.codinux.log.loki.quarkus.config.fields.QuarkusFieldConfig;

public interface PodNameConfig extends QuarkusFieldConfig {

    /**
     * If the Pod name should be included in index.
     *
     * Defaults to {@code false} to avoid labels with high cardinality which is bad for Loki.
     */
    @Override
    @WithDefault(KubernetesFieldsConfig.PodNameDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the Pod name index field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(KubernetesFieldsConfig.PodNameDefaultFieldName)
    String fieldName();

}