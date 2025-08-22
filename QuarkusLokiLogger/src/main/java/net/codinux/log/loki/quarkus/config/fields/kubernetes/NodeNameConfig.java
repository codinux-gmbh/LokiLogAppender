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
public interface NodeNameConfig extends QuarkusFieldConfig {

    /**
     * If the node name should be included in index.
     */
    @Override
    @WithDefault(KubernetesFieldsConfig.NodeNameDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the node name index field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(KubernetesFieldsConfig.NodeNameDefaultFieldName)
    String fieldName();

}