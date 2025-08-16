package net.codinux.log.loki.config.field

open class FieldWithValueConfig(
    name: String,
    include: IncludeField = IncludeField.No,
    open var value: String? = null,
) : FieldConfig(name, include)