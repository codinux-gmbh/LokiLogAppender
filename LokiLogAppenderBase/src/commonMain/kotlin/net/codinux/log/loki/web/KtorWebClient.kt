package net.codinux.log.loki.web

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.*
import net.codinux.log.data.KtorStreamContent
import net.codinux.log.statelogger.AppenderStateLogger

class KtorWebClient(
    private val stateLogger: AppenderStateLogger,
    lokiPushApiUrl: String,
    username: String?,
    password: String?,
    tenantId: String?
) : WebClient {

    companion object {
        private val JsonContentType = ContentType.parse("application/json")
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
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

        if (username != null && password != null) {
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

    override suspend fun post(body: Any): Boolean {
        val response = client.request {
            this.method = HttpMethod.Post

            if (KtorStreamContent.isSupported) {
                setBody(KtorStreamContent(body, KtorStreamContent.supportsGZip))
            } else {
                setBody(body)
            }
        }

        if (response.status.isSuccess() == false) {
            stateLogger.error("Could not push logs to Loki, response was: ${response.status}, ${response.bodyAsText()}")
        }

        return response.status.isSuccess()
    }
}