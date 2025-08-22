package net.codinux.log.loki.config.fields

import net.codinux.log.loki.config.LogFieldsConfig

open class StacktraceFieldConfig(
    name: String,
    include: IncludeField = IncludeField.No,
    var maxFieldLength: Int = LogFieldsConfig.StacktraceMaxFieldLengthDefaultValue,
) : FieldConfig(name, include) {

    constructor() : this("", IncludeField.No) // required for Joran (Logback)

}