package net.codinux.log.loki.quarkus.config.fields.kubernetes;

import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.config.fields.kubernetes.KubernetesFieldsConfig;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;
import net.codinux.log.loki.quarkus.config.fields.QuarkusFieldConfig;

public interface ContainerNameConfig extends QuarkusFieldConfig {

    /**
     * If the container name should be included in index.
     *
     * Defaults to {@code false}.
     */
    @Override
    @WithDefault(KubernetesFieldsConfig.ContainerNameDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the container name index field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(KubernetesFieldsConfig.ContainerNameDefaultFieldName)
    String fieldName();

}