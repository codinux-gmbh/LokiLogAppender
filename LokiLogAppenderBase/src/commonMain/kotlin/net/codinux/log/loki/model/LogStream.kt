package net.codinux.log.loki.model

import kotlinx.serialization.Serializable

@Serializable
open class LogStream {

    /**
     * The labels - indexed key-value pairs - which are common to all [LogStreamEntry] in [values].
     */
    open var stream: MutableMap<String, String?> = mutableMapOf()
        protected set

    // in our implementation values always has exact one element
    open val values: List<LogStreamEntry> = listOf(LogStreamEntry())

    open fun set(timestamp: String, logLine: String, structuredMetadata: Map<String, String> = emptyMap()) {
        values[0].set(timestamp, logLine, structuredMetadata)
    }

    override fun toString(): String {
        return "$values: $stream"
    }

}