package net.codinux.log.loki.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.http.content.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import java.util.zip.GZIPOutputStream

actual class KtorStreamContent actual constructor(
    private val content: Any,
    private val gzipContent: Boolean
) : OutgoingContent.WriteChannelContent() {

    actual companion object {

        actual val isSupported = true

        actual val supportsGZip = true

        // to not instantiate it per web call / KtorStreamContent instance creation
        private val objectMapper = ObjectMapper().apply {
            registerModule(KotlinModule.Builder().build())
        }

    }

    override suspend fun writeTo(channel: ByteWriteChannel) {
        var outputStream = channel.toOutputStream()
        if (gzipContent) {
            outputStream = GZIPOutputStream(outputStream)
        }

        objectMapper.writeValue(outputStream, content)

        outputStream.flush()
        outputStream.close()
    }

}