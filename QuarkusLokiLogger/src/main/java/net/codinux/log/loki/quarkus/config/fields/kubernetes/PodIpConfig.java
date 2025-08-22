package net.codinux.log.loki.quarkus.config.fields.kubernetes;

import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.config.fields.kubernetes.KubernetesFieldsConfig;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;
import net.codinux.log.loki.quarkus.config.fields.QuarkusFieldConfig;

public interface PodIpConfig extends QuarkusFieldConfig {

    /**
     * If the Pod IP address should be included in index.
     */
    @Override
    @WithDefault(KubernetesFieldsConfig.PodIpDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the Pod IP address index field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(KubernetesFieldsConfig.PodIpDefaultFieldName)
    String fieldName();

}