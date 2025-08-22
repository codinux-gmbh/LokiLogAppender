package net.codinux.log.loki.config.fields

open class StacktraceFieldConfig(
    name: String,
    include: IncludeField = IncludeField.No,
    var maxFieldLength: Int = LogFieldsConfig.StacktraceDefaultMaxFieldLengthValue,
) : FieldConfig(name, include) {

    constructor() : this("", IncludeField.No) // required for Joran (Logback)

}