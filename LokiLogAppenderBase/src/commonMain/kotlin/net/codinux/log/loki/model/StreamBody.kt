package net.codinux.log.loki.model

import kotlinx.serialization.Serializable

@Serializable
open class StreamBody(
    var streams: List<Stream> = listOf()
) {

    override fun toString(): String {
        return "$streams"
    }
}