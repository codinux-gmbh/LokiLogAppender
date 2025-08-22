package net.codinux.log.loki.quarkus.config.fields.kubernetes;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import net.codinux.log.loki.config.fields.IncludeField;
import net.codinux.log.loki.config.fields.kubernetes.KubernetesFieldsConfig;
import net.codinux.log.loki.quarkus.config.converter.IncludeFieldConverter;
import net.codinux.log.loki.quarkus.config.fields.QuarkusPrefixFieldConfig;
import net.codinux.log.quarkus.converter.FieldNamePrefixConverter;

@ConfigGroup
public interface KubernetesAnnotationsConfig extends QuarkusPrefixFieldConfig {

    /**
     * If Kubernetes annotations should be included in Elasticsearch index.
     */
    @Override
    @WithDefault(KubernetesFieldsConfig.AnnotationsDefaultIncludeValueString)
    @WithConverter(IncludeFieldConverter.class)
    IncludeField include();

    /**
     * Sets a prefix for all Kubernetes annotations. Defaults to "annotation".
     *
     * Empty string or special value "off" turns prefix off.
     */
    @Override
    @WithDefault(KubernetesFieldsConfig.AnnotationsPrefixDefaultValue)
    @WithConverter(FieldNamePrefixConverter.class)
    String prefix();

}