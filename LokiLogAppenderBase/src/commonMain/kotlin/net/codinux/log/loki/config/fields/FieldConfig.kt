package net.codinux.log.loki.config.fields

open class FieldConfig(
    open var name: String,
    open var include: IncludeField = IncludeField.No
) {

    constructor() : this("", IncludeField.No) // required for Joran (Logback)


    open val isIncluded: Boolean
        get() = include.isIncluded

    open val logAsLabel: Boolean
        get() = include.logAsLabel

    open val logAsStructuredMetadata: Boolean
        get() = include.logAsStructuredMetadata

    open fun isIncludedAs(include: IncludeField): Boolean =
        this.include == include

    open fun setInclude(include: Boolean) {
        this.include = if (include) {
            IncludeField.Label
        } else {
            IncludeField.No
        }
    }

    override fun toString() = "$name include $include"
}