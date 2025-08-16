package net.codinux.log.loki.config.field

open class FieldConfig(
    open var name: String,
    open var include: IncludeField = IncludeField.No
) {
    open val isIncluded: Boolean
        get() = include == IncludeField.No

    open fun setInclude(include: Boolean) {
        this.include = if (include) {
            IncludeField.Label
        } else {
            IncludeField.No
        }
    }

    override fun toString() = "$name include $include"
}