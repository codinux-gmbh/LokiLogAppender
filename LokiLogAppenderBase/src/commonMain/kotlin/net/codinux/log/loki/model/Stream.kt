package net.codinux.log.loki.model

import kotlinx.serialization.Serializable

@Serializable
open class Stream {

    open var stream: MutableMap<String, String?> = mutableMapOf()
        protected set

    // in our implementation values always has exact one element
    open val values: List<Values> = listOf(Values())

    open fun set(timestamp: String, message: String, structuredMetadata: Map<String, String> = emptyMap()) {
        values[0].set(timestamp, message, structuredMetadata)
    }

    override fun toString(): String {
        return "$values: $stream"
    }

}