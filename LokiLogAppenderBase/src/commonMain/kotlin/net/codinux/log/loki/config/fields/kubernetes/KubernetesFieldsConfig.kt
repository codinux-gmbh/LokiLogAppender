package net.codinux.log.loki.config.fields.kubernetes

import net.codinux.log.loki.config.fields.FieldConfig
import net.codinux.log.loki.config.fields.LogFieldsConfig.Companion.False
import net.codinux.log.loki.config.fields.LogFieldsConfig.Companion.Label
import net.codinux.log.loki.config.fields.LogFieldsConfig.Companion.No
import net.codinux.log.loki.config.fields.LogFieldsConfig.Companion.StructuredMetadata
import net.codinux.log.loki.config.fields.LogFieldsConfig.Companion.asInclude

open class KubernetesFieldsConfig(

    open var includeKubernetesInfo: Boolean = IncludeKubernetesInfoDefaultValue,
    open var kubernetesFieldsPrefix: String? = KubernetesFieldsPrefixDefaultValue,

    open var namespace: FieldConfig = FieldConfig(NamespaceDefaultFieldName, NamespaceDefaultIncludeValue),

    open var podName: FieldConfig = FieldConfig(PodNameDefaultFieldName, PodNameDefaultIncludeValue),
    open var podIp: FieldConfig = FieldConfig(PodIpDefaultFieldName, PodIpDefaultIncludeValue),
    open var podUid: FieldConfig = FieldConfig(PodUidDefaultFieldName, PodUidDefaultIncludeValue),

    open var containerName: FieldConfig = FieldConfig(ContainerNameDefaultFieldName, ContainerNameDefaultIncludeValue),

    open var imageName: FieldConfig = FieldConfig(ImageNameDefaultFieldName, ImageNameDefaultIncludeValue),

    open var nodeName: FieldConfig = FieldConfig(NodeNameDefaultFieldName, NodeNameDefaultIncludeValue),
    open var nodeIp: FieldConfig = FieldConfig(NodeIpDefaultFieldName, NodeIpDefaultIncludeValue),


    // TODO: add containerId, imageId, startTime, restartCount, labels, annotations
) {

    companion object {

        const val IncludeKubernetesInfoDefaultValue = False
        const val IncludeKubernetesInfoDefaultValueString = IncludeKubernetesInfoDefaultValue.toString()
        const val KubernetesFieldsPrefixDefaultValue: String = ""

        const val NamespaceDefaultFieldName = "namespace"
        const val NamespaceDefaultIncludeValueString = Label
        val NamespaceDefaultIncludeValue = NamespaceDefaultIncludeValueString.asInclude()

        const val PodNameDefaultFieldName = "pod"
        const val PodNameDefaultIncludeValueString = StructuredMetadata
        val PodNameDefaultIncludeValue = PodNameDefaultIncludeValueString.asInclude()

        const val PodIpDefaultFieldName = "podIp"
        const val PodIpDefaultIncludeValueString = No
        val PodIpDefaultIncludeValue = PodIpDefaultIncludeValueString.asInclude()

        const val PodUidDefaultFieldName = "podUid"
        const val PodUidDefaultIncludeValueString = No
        val PodUidDefaultIncludeValue = PodUidDefaultIncludeValueString.asInclude()

        const val ContainerNameDefaultFieldName = "container"
        const val ContainerNameDefaultIncludeValueString = No
        val ContainerNameDefaultIncludeValue = ContainerNameDefaultIncludeValueString.asInclude()

        const val ImageNameDefaultFieldName = "image"
        const val ImageNameDefaultIncludeValueString = No
        val ImageNameDefaultIncludeValue = ImageNameDefaultIncludeValueString.asInclude()

        const val NodeNameDefaultFieldName = "node"
        const val NodeNameDefaultIncludeValueString = No
        val NodeNameDefaultIncludeValue = NodeNameDefaultIncludeValueString.asInclude()

        const val NodeIpDefaultFieldName = "nodeIp"
        const val NodeIpDefaultIncludeValueString = No
        val NodeIpDefaultIncludeValue = NodeIpDefaultIncludeValueString.asInclude()

    }

}