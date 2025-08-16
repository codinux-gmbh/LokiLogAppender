package net.codinux.log.loki.config.field

open class PrefixFieldConfig(
    open var prefix: String,
    open var include: IncludeField = IncludeField.No
) {
    open val isIncluded: Boolean
        get() = include == IncludeField.No

    override fun toString() = "$prefix include $include"
}