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
import net.codinux.log.config.WriterConfig
import net.codinux.log.data.KtorStreamContent
import net.codinux.log.statelogger.AppenderStateLogger

class KtorWebClient(
    private val stateLogger: AppenderStateLogger,
    lokiPushApiUrl: String,
    tenantId: String?,
    config: WriterConfig
) : WebClient {

    companion object {
        private val JsonContentType = ContentType.parse("application/json")
    }

    private val client = HttpClient {
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

            if (KtorStreamContent.isSupported && KtorStreamContent.supportsGZip) {
                headers.append("Content-Encoding", "gzip")
            }
        }

        config.username?.let { username ->
            config.password?.let { password ->
                install(Auth) {
                    basic {
                        credentials {
                            BasicAuthCredentials(username, password)
                        }
                        sendWithoutRequest { request ->
                            request.url.buildString() == lokiPushApiUrl
                        }
                    }
                }
            }
        }
    }

    override suspend fun post(body: Any, logError: Boolean): Int {
        val response = client.request {
            this.method = HttpMethod.Post

            if (KtorStreamContent.isSupported) {
                setBody(KtorStreamContent(body, KtorStreamContent.supportsGZip))
            } else {
                setBody(body)
            }
        }

        if (logError && response.status.isSuccess() == false) {
            stateLogger.error("Could not push logs to Loki: ${response.status} ${response.bodyAsText()}. Request body was:\n$body")
        }

        return response.status.value
    }
}