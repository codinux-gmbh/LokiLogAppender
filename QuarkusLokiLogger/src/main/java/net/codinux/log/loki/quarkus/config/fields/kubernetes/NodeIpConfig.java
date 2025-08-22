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
public interface NodeIpConfig extends QuarkusFieldConfig {

    /**
     * If the node IP address should be included in index.
     */
    @Override
    @WithDefault(KubernetesFieldsConfig.NodeIpDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * The name of the node IP address index field.
     */
    @Override
    @WithName("fieldname")
    @WithDefault(KubernetesFieldsConfig.NodeIpDefaultFieldName)
    String fieldName();

}