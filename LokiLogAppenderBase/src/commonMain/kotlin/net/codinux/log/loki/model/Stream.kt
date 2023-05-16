package net.codinux.log.loki.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
open class Stream {

    open var stream: MutableMap<String, String?> = mutableMapOf()
        protected set

    @Transient
    /**
     * These are labels that are not included in every log record like MDC values
     */
    open val dynamicLabels = mutableListOf<String>()

    // in our implementation values always has exact one element
    open val values: List<Values> = listOf(Values())

    open fun set(timestamp: String, message: String) {
        values[0].set(timestamp, message)
    }

    override fun toString(): String {
        return "$values: $stream"
    }

}