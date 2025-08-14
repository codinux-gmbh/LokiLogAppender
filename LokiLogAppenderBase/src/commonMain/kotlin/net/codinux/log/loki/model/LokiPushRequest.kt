package net.codinux.log.loki.model

import kotlinx.serialization.Serializable

// classes and properties named according to their official Protobuf file:
// https://github.com/grafana/loki/blob/main/pkg/push/push.proto
@Serializable
open class LokiPushRequest(
    var streams: List<LogStream> = listOf()
) {

    override fun toString(): String {
        return "$streams"
    }
}