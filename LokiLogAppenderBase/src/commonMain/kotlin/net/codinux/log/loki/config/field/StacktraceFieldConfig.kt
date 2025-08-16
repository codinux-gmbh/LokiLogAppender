package net.codinux.log.loki.config.field

import net.codinux.log.loki.config.LogFieldsConfig

class StacktraceFieldConfig(
    name: String,
    include: IncludeField = IncludeField.No,
    var maxFieldLength: Int = LogFieldsConfig.StacktraceMaxFieldLengthDefaultValue,
) : FieldConfig(name, include)