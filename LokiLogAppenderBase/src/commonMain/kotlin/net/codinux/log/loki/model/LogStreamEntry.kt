package net.codinux.log.loki.model

import kotlinx.serialization.Serializable
import net.codinux.log.loki.serialization.LogStreamEntrySerializer

@Serializable(with = LogStreamEntrySerializer::class)
// Loki's values are not safely typed. The first value is the timestamp in RFC3339 or RFC3339Nano format, the second the log line
open class LogStreamEntry : OpenArrayList<Any>(listOf("", "", mutableMapOf<String, String>())) {

    /**
     * The timestamp of this log entry in nanoseconds since Unix epoch.
     */
    open var timestamp: String = ""
        protected set

    open var logLine: String = ""
        protected set

    /**
     * Optional additional metadata as key-value pairs that opposed to labels are not indexed
     * and therefore don't raise cardinality (label explosion).
     */
    open var structuredMetadata: MutableMap<String, String?> = mutableMapOf()
        protected set

    init {
        this[2] = structuredMetadata
    }


    open fun set(timestampInNanosSinceUnixEpoch: String, logLine: String) {
        this[0] = timestampInNanosSinceUnixEpoch
        this[1] = logLine

        this.timestamp = timestampInNanosSinceUnixEpoch
        this.logLine = logLine
    }

    override fun toString(): String {
        return "$timestamp $logLine"
    }

}