package net.codinux.log.loki.config

import net.codinux.log.config.CostlyFieldsConfig
import net.codinux.log.config.KubernetesFieldsConfig
import net.codinux.log.config.LogAppenderFieldsConfig.Companion.IncludeKubernetesInfoDefaultValue
import net.codinux.log.loki.config.field.FieldConfig
import net.codinux.log.loki.config.field.FieldWithValueConfig
import net.codinux.log.loki.config.field.IncludeField
import net.codinux.log.loki.config.field.PrefixFieldConfig
import net.codinux.log.loki.config.field.StacktraceFieldConfig

open class LogFieldsConfig(

    open var logLevel: FieldConfig = FieldConfig(LogLevelDefaultFieldName, LogLevelDefaultIncludeValue),

    open var logger: FieldConfig = FieldConfig(LoggerNameDefaultFieldName, LoggerNameDefaultIncludeValue),
    open var loggerClass: FieldConfig = FieldConfig(LoggerClassNameDefaultFieldName, LoggerClassNameDefaultIncludeValue),

    open var thread: FieldConfig = FieldConfig(ThreadNameDefaultFieldName, ThreadNameDefaultIncludeValue),

    open var stacktrace: StacktraceFieldConfig = StacktraceFieldConfig(StacktraceDefaultFieldName, StacktraceDefaultIncludeValue, StacktraceMaxFieldLengthDefaultValue),


    open var appName: FieldWithValueConfig = FieldWithValueConfig(
        AppNameDefaultFieldName,
        AppNameDefaultIncludeValue,
        AppNameDefaultValue
    ),
    open var appVersion: FieldWithValueConfig = FieldWithValueConfig(
        AppVersionDefaultFieldName,
        AppVersionDefaultIncludeValue,
        AppVersionDefaultValue
    ),

    open var job: FieldWithValueConfig = FieldWithValueConfig(
        JobNameDefaultFieldName,
        JobNameDefaultIncludeValue,
        JobNameDefaultValue
    ),

    open var hostName: FieldConfig = FieldConfig(HostNameDefaultFieldName, HostNameDefaultIncludeValue),
    open var hostIp: FieldConfig = FieldConfig(HostIpDefaultFieldName, HostIpDefaultIncludeValue),


    open var mdc: PrefixFieldConfig = PrefixFieldConfig(MdcFieldsPrefixDefaultValue, MdcDefaultIncludeValue),

    open var marker: FieldConfig = FieldConfig(MarkerDefaultFieldName, MarkerDefaultIncludeValue),

    open var ndc: FieldConfig = FieldConfig(NdcDefaultFieldName, NdcDefaultIncludeValue),


    // TODO: extract to object KubernetesFieldsConfig(include: Boolean, prefix: String, <fields>)
    open var includeKubernetesInfo: Boolean = IncludeKubernetesInfoDefaultValue,
    open var kubernetesFieldsPrefix: String? = KubernetesFieldsPrefixDefaultValue,

    open var kubernetesFields: KubernetesFieldsConfig = KubernetesFieldsConfig(),

    ) : CostlyFieldsConfig {

    override val logsLoggerName: Boolean
        get() = logger.isIncluded|| loggerClass.isIncluded

    override val logsThreadName: Boolean
        get() = thread.isIncluded

    override val logsException: Boolean
        get() = stacktrace.isIncluded

    override val logsMdc: Boolean
        get() = mdc.include == IncludeField.No

    override val logsMarker: Boolean
        get() = marker.isIncluded

    override val logsNdc: Boolean
        get() = ndc.isIncluded


    companion object {

        private const val True = true
        private const val False = false

        const val No = "No"
        const val Label = "Label"
        const val StructuredMetadata = "StructuredMetadata"


        /*          Log Event fields            */

        const val LogLevelDefaultFieldName = "level"
        // we have to do it this way round as we need a const String value for Quarkus annotation
        const val LogLevelDefaultIncludeValueString = Label
        val LogLevelDefaultIncludeValue = LogLevelDefaultIncludeValueString.asInclude()

        const val LoggerNameDefaultFieldName = "logger"
        const val LoggerNameDefaultIncludeValueString = StructuredMetadata
        val LoggerNameDefaultIncludeValue = LoggerNameDefaultIncludeValueString.asInclude()

        const val LoggerClassNameDefaultFieldName = "loggerClass"
        const val LoggerClassNameDefaultIncludeValueString = No
        val LoggerClassNameDefaultIncludeValue = LoggerClassNameDefaultIncludeValueString.asInclude()

        const val ThreadNameDefaultFieldName = "thread"
        const val ThreadNameDefaultIncludeValueString = No // this is opposed to other log systems where it's included by default
        val ThreadNameDefaultIncludeValue = ThreadNameDefaultIncludeValueString.asInclude()

        const val StacktraceDefaultFieldName = "stacktrace"
        const val StacktraceDefaultIncludeValueString = StructuredMetadata
        val StacktraceDefaultIncludeValue = StacktraceDefaultIncludeValueString.asInclude()
        const val StacktraceMaxFieldLengthDefaultValue = 32766 - 100 // subtract a little buffer
        const val StacktraceMaxFieldLengthDefaultValueString = StacktraceMaxFieldLengthDefaultValue.toString()


        /*          Log Event metadata          */

        const val MdcFieldsPrefixDefaultValue: String = "mdc"
        const val MdcDefaultIncludeValueString = StructuredMetadata
        val MdcDefaultIncludeValue = MdcDefaultIncludeValueString.asInclude()

        const val MarkerDefaultFieldName: String = "marker"
        const val MarkerDefaultIncludeValueString = StructuredMetadata
        val MarkerDefaultIncludeValue = MarkerDefaultIncludeValueString.asInclude()

        const val NdcDefaultFieldName: String = "ndc"
        const val NdcDefaultIncludeValueString = StructuredMetadata
        val NdcDefaultIncludeValue = NdcDefaultIncludeValueString.asInclude()


        /*          App metadata            */

        const val AppNameDefaultFieldName = "app"
        const val AppNameDefaultIncludeValueString = Label
        val AppNameDefaultIncludeValue = AppNameDefaultIncludeValueString.asInclude()
        val AppNameDefaultValue: String? = null
        const val AppNameDefaultValueString = "null"

        const val AppVersionDefaultFieldName = "version"
        const val AppVersionDefaultIncludeValueString = No
        val AppVersionDefaultIncludeValue = AppVersionDefaultIncludeValueString.asInclude()
        val AppVersionDefaultValue: String? = null
        const val AppVersionDefaultValueString = "null"

        const val JobNameDefaultFieldName = "job"
        const val JobNameDefaultIncludeValueString = Label
        val JobNameDefaultIncludeValue = JobNameDefaultIncludeValueString.asInclude()
        const val JobNameDefaultValue: String = "LokiLogger"


        /*          Host                */

        const val HostNameDefaultFieldName = "host"
        const val HostNameDefaultIncludeValueString = No
        val HostNameDefaultIncludeValue = HostNameDefaultIncludeValueString.asInclude()

        const val HostIpDefaultFieldName = "hostIP"
        const val HostIpDefaultIncludeValueString = No
        val HostIpDefaultIncludeValue = HostIpDefaultIncludeValueString.asInclude()


        /*          Kubernetes          */

        const val KubernetesFieldsPrefixDefaultValue: String = "k8s"
        const val KubernetesInfoDefaultIncludeValue = False
        const val KubernetesInfoDefaultIncludeValueString = KubernetesInfoDefaultIncludeValue.toString()


        private fun String.asInclude(): IncludeField = when (this) {
            Label -> IncludeField.Label
            StructuredMetadata -> IncludeField.StructuredMetadata
            No -> IncludeField.No
            else -> IncludeField.No
        }
    }

}