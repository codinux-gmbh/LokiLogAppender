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

    // only works as in our implementation values always has exact one element
    open val structuredMetadata: MutableMap<String, String?> = values[0].structuredMetadata

    open fun set(timestamp: String, logLine: String) {
        values[0].set(timestamp, logLine)
    }

    override fun toString(): String {
        return "$values: $stream"
    }

}