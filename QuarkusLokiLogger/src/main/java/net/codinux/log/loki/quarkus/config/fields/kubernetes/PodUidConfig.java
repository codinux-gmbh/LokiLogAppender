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
public interface PodUidConfig extends QuarkusFieldConfig {

    /**
     * If the Pod UID should be included in index.
     */
    @Override
    @WithDefault(KubernetesFieldsConfig.PodUidDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the Pod UID index field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(KubernetesFieldsConfig.PodUidDefaultFieldName)
    String fieldName();

}