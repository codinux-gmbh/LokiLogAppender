package net.codinux.log.loki.config.fields

open class FieldWithValueConfig(
    name: String,
    include: IncludeField = IncludeField.No,
    open var value: String? = null,
) : FieldConfig(name, include) {

    constructor() : this("", IncludeField.No, null) // required for Joran (Logback)


    override fun toString() = "${super.toString()}: $value"
}