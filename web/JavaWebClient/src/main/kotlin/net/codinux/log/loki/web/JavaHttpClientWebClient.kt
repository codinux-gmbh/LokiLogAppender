package net.codinux.log.loki.web

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import net.codinux.log.config.WriterConfig
import net.codinux.log.loki.LokiLogWriter.Companion.getLokiPushApiUrl
import net.codinux.log.loki.config.LokiLogAppenderConfig
import net.codinux.log.statelogger.AppenderStateLogger
import java.io.ByteArrayOutputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.*
import java.util.zip.GZIPOutputStream

open class JavaHttpClientWebClient(
    protected val stateLogger: AppenderStateLogger,
    lokiPushApiUrl: String,
    tenantId: String?,
    config: WriterConfig
) : WebClient {

    companion object {
        fun of(config: LokiLogAppenderConfig, stateLogger: AppenderStateLogger): JavaHttpClientWebClient =
            JavaHttpClientWebClient(stateLogger, getLokiPushApiUrl(config.writer.hostUrl), config.tenantId, config.writer)
    }


    protected val client = HttpClient.newBuilder().apply {
        config.connectTimeoutMillis?.let {
            connectTimeout(Duration.ofMillis(it))
        }
    }.build()

    protected val requestBuilder = HttpRequest
        .newBuilder(URI.create(lokiPushApiUrl))
        .header("Content-Type", "application/json")
        .header("Content-Encoding", "gzip")
        .header("Accept-Encoding", "gzip")
        .apply {
            if (tenantId != null) {
                header("X-Scope-OrgID", tenantId)
            }


            config.username?.let { username ->
                config.password?.let { password ->
                    val authHeader = "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray())
                    header("Authorization", authHeader)
                }
            }

            config.requestTimeoutMillis?.let {
                timeout(Duration.ofMillis(it))
            }
        }

    protected val objectMapper = ObjectMapper().apply {
        findAndRegisterModules()
    }


    override suspend fun post(body: Any, logError: Boolean): Pair<Int, String?> = withContext(Dispatchers.IO) {
        val bodyAsString = if (body is String) body else objectMapper.writeValueAsString(body)

        val request = requestBuilder.POST(HttpRequest.BodyPublishers.ofByteArray(gzip(bodyAsString))).build()

//        val bodyHandler = JavaHttpResponseBodyHandler()
        val bodyHandler = HttpResponse.BodyHandlers.ofString()
        val response = client.sendAsync(request, bodyHandler).await()
        val status = response.statusCode()

        status to (if (status < 300) null else response.body())
    }

    protected open fun gzip(body: String): ByteArray {
        val outputStream = ByteArrayOutputStream()
        GZIPOutputStream(outputStream).use { it.write(body.toByteArray()) }
        return outputStream.toByteArray()
    }

}