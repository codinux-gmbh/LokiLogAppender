package net.codinux.log.loki.web

expect class KtorStreamContent(content: Any, gzipContent: Boolean) {

    companion object {

        val isSupported: Boolean

        val supportsGZip: Boolean

    }

}