package net.codinux.log.loki.web

expect class KtorRequestBodyCompression(content: Any) {

    companion object {

        val supportsGZip: Boolean

    }

}