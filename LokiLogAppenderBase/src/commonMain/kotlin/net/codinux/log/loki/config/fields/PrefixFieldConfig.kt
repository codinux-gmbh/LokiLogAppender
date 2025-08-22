package net.codinux.log.loki.config.fields

open class PrefixFieldConfig(
    open var prefix: String,
    open var include: IncludeField = IncludeField.No
) {

    constructor() : this("", IncludeField.No) // required for Joran (Logback)

    open val isIncluded: Boolean
        get() = include.isIncluded

    open val logAsLabel: Boolean
        get() = include.logAsLabel

    open val logAsStructuredMetadata: Boolean
        get() = include.logAsStructuredMetadata


    override fun toString() = "$prefix include $include"
}