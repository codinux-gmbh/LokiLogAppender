package net.codinux.log.loki.web

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.codinux.log.config.WriterConfig
import net.codinux.log.loki.LokiLogWriter.Companion.getLokiPushApiUrl
import net.codinux.log.loki.config.LokiLogAppenderConfig
import net.codinux.log.statelogger.AppenderStateLogger
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

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


    protected val client = HttpClient.newHttpClient()

    protected val requestBuilder = HttpRequest
        .newBuilder(URI.create(lokiPushApiUrl))
        .header("Content-Type", "application/json")
        .apply {
            if (tenantId != null) {
                header("X-Scope-OrgID", tenantId)
            }
        }
        // TODO: configure credentials
        // TODO: configure timeouts
        // TODO: configure gzip

    protected val objectMapper = ObjectMapper().apply {
        findAndRegisterModules()
    }


    override suspend fun post(body: Any, logError: Boolean): Int = withContext(Dispatchers.IO) {
        val bodyAsString = if (body is String) body else objectMapper.writeValueAsString(body)

        val request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(bodyAsString)).build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (logError && response.statusCode() !in 200..299) {
            stateLogger.error("Could not push logs to Loki: ${response.statusCode()} ${response.body()}. Request body was:\n$body")
        }

        response.statusCode()
    }

}