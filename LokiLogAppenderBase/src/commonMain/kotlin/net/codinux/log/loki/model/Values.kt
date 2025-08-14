package net.codinux.log.loki.model

import kotlinx.serialization.Serializable
import net.codinux.log.loki.serialization.ValuesSerializer

@Serializable(with = ValuesSerializer::class)
// Loki's values are not safely typed. The first value is the timestamp in RFC3339 or RFC3339Nano format, the second the log line
open class Values : OpenArrayList<Any>(listOf("", "", mapOf<String, String>())) {

    open var timestamp: String = ""
        protected set

    open var message: String = ""
        protected set

    open var structuredMetadata: Map<String, String> = emptyMap()
        protected set


    open fun set(timestamp: String, message: String, structuredMetadata: Map<String, String> = emptyMap()) {
        this[0] = timestamp
        this[1] = message
        this[2] = structuredMetadata

        this.timestamp = timestamp
        this.message = message
        this.structuredMetadata = structuredMetadata
    }

    override fun toString(): String {
        return "$timestamp $message"
    }




}