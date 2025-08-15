package net.codinux.log.loki.web

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import net.codinux.log.auth.Authentication
import net.codinux.log.auth.BasicAuthAuthentication
import net.codinux.log.config.WriterConfig
import net.codinux.log.loki.LokiLogWriter.Companion.getLokiPushApiUrl
import net.codinux.log.loki.config.LokiLogAppenderConfig
import net.codinux.log.statelogger.AppenderStateLogger

open class KtorWebClient(
    private val stateLogger: AppenderStateLogger,
    lokiPushApiUrl: String,
    authentication: Authentication? = null,
    tenantId: String?,
    config: WriterConfig
) : WebClient {

    companion object {
        val JsonContentType = ContentType.parse("application/json")

        fun of(config: LokiLogAppenderConfig, stateLogger: AppenderStateLogger): KtorWebClient =
            KtorWebClient(stateLogger, getLokiPushApiUrl(config.hostUrl), config.getAuthentication(), config.tenantId, config.writer)
    }


    protected val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }

        install(HttpTimeout) {
            config.connectTimeoutMillis?.let {
                connectTimeoutMillis = it
            }
            config.requestTimeoutMillis?.let {
                requestTimeoutMillis = it
            }
        }

        defaultRequest {
            url(lokiPushApiUrl)
            if (tenantId != null) {
                header("X-Scope-OrgID", tenantId)
            }

            contentType(JsonContentType)

            if (KtorRequestBodyCompression.supportsGZip) {
                headers.append("Content-Encoding", "gzip")
            }
        }

        authentication?.let {
            install(Auth) {
                (authentication as? BasicAuthAuthentication)?.let { basicAuth ->
                    basic {
                        credentials {
                            BasicAuthCredentials(basicAuth.username, basicAuth.password)
                        }
                        sendWithoutRequest { request ->
                            request.url.buildString() == lokiPushApiUrl
                        }
                    }
                }
            }
        }
    }

    override suspend fun post(body: Any, logError: Boolean): Pair<Int, String?> {
        val response = client.request {
            this.method = HttpMethod.Post

            if (KtorRequestBodyCompression.supportsGZip) {
                setBody(KtorRequestBodyCompression(body))
            } else {
                setBody(body)
            }
        }


        return response.status.value to (if (response.status.isSuccess()) null else response.bodyAsText())
    }
}