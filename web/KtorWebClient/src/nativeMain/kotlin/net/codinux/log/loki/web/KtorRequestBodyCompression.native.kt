package net.codinux.log.loki.web

actual class KtorRequestBodyCompression actual constructor(content: Any) {

    actual companion object {

        actual val supportsGZip = false

    }

}