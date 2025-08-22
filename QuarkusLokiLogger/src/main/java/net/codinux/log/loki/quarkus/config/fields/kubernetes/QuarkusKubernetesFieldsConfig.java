package net.codinux.log.loki.quarkus.config.fields.kubernetes;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.config.fields.kubernetes.KubernetesFieldsConfig;
import net.codinux.log.quarkus.converter.FieldNamePrefixConverter;

@ConfigGroup
public interface QuarkusKubernetesFieldsConfig {

    /**
     * If Pod and Kubernetes info should be included in Loki.
     */
    @WithDefault(KubernetesFieldsConfig.IncludeKubernetesInfoDefaultValueString)
    boolean include();

    /**
     * Sets a prefix for all Kubernetes info keys.
     *
     * Empty string or special value "off" turns prefix off.
     *
     * Defaults to an empty string (= no prefix). Others prefer "k8s".
     */
    @WithDefault(KubernetesFieldsConfig.KubernetesFieldsPrefixDefaultValue)
    @WithConverter(FieldNamePrefixConverter.class)
    String prefix();



    /**
     * Config for the Kubernetes namespace the Pod is running in.
     */
    NamespaceConfig namespace();


    /**
     * Config for the Pod name index field.
     */
    @WithName("podname")
    PodNameConfig podName();

    /**
     * Config for the POD IP index field.
     */
    @WithName("podip")
    PodIpConfig podIp();

    /**
     * Config for the Pod UID index field.
     */
    @WithName("poduid")
    PodUidConfig podUid();


    /**
     * Config for the container name index field.
     */
    @WithName("containername")
    ContainerNameConfig containerName();


    /**
     * Config for the image name index field.
     */
    @WithName("imagename")
    ImageNameConfig imageName();


    /**
     * Config for the node name index field.
     */
    @WithName("nodename")
    NodeNameConfig nodeName();

    /**
     * Config for the node IP index field.
     */
    @WithName("nodeip")
    NodeIpConfig nodeIp();

//
//    /**
//     * Config for the container start time index field.
//     */
//    @WithName("starttime")
//    StartTimeConfig startTime();
//
//    /**
//     * Config for the container restart count index field.
//     */
//    @WithName("restartcount")
//    RestartCountConfig restartCount();


//    /**
//     * Config for the container id index field.
//     */
//    @WithName("containerid")
//    ContainerIdConfig containerId();
//
//    /**
//     * Config for the image id index field.
//     */
//    @WithName("imageid")
//    ImageIdConfig imageId();
//
//
//    /**
//     * Config for the Kubernetes labels index fields.
//     */
//    KubernetesLabelsConfig labels();
//
//
//    /**
//     * Config for the Kubernetes annotations index fields.
//     */
//    KubernetesAnnotationsConfig annotations();

}