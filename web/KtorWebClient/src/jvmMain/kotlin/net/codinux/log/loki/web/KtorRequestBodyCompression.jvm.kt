package net.codinux.log.loki.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.http.content.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.zip.GZIPOutputStream

actual class KtorRequestBodyCompression actual constructor(
    private val content: Any,
) : OutgoingContent.WriteChannelContent() {

    actual companion object {

        actual val supportsGZip = true

        // to not instantiate it per web call / KtorStreamContent instance creation
        private val objectMapper = ObjectMapper().apply {
            registerModule(KotlinModule.Builder().build())
        }

    }


    override suspend fun writeTo(channel: ByteWriteChannel) = withContext(Dispatchers.IO) {
        GZIPOutputStream(channel.toOutputStream()).use { outputStream ->
            objectMapper.writeValue(outputStream, content)
        }
    }

}