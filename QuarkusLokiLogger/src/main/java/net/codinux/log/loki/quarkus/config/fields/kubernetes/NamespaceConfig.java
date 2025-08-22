package net.codinux.log.loki.quarkus.config.fields.kubernetes;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.config.fields.kubernetes.KubernetesFieldsConfig;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;
import net.codinux.log.loki.quarkus.config.fields.QuarkusFieldConfig;

@ConfigGroup
public interface NamespaceConfig extends QuarkusFieldConfig {

    /**
     * If Kubernetes namespace the Pod is running in should be included in index.
     */
    @Override
    @WithDefault(KubernetesFieldsConfig.NamespaceDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the Kubernetes namespace index field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(KubernetesFieldsConfig.NamespaceDefaultFieldName)
    String fieldName();

}