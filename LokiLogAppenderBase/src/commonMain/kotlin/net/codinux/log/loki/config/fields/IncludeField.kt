package net.codinux.log.loki.config.fields

enum class IncludeField {
    No,

    Label,

    StructuredMetadata,
    ;


    val isIncluded: Boolean
        get() = this != No

    val logAsLabel: Boolean
        get() = this == Label

    val logAsStructuredMetadata: Boolean
        get() = this == StructuredMetadata
}