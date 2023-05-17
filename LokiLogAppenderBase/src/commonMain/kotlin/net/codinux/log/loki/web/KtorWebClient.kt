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

class KtorWebClient(lokiPushApiUrl: String, username: String?, password: String?, tenantId: String?) : WebClient {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }

        defaultRequest {
            url(lokiPushApiUrl)
            if (tenantId != null) {
                header("X-Scope-OrgID", tenantId)
            }

            KtorStreamContent.additionalHeaders.forEach { (name, value) ->
                headers.append(name, value)
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

    override suspend fun post(url: String, body: Any, contentType: String, headers: Map<String, String>): Boolean {
        val response = client.request {
            this.method = HttpMethod.Post
            contentType(ContentType.parse(contentType))

            if (KtorStreamContent.isSupported) {
                setBody(KtorStreamContent(body))
            } else {
                setBody(body)
            }

            headers.forEach { (name, value) -> this.headers.append(name, value) }
        }

        if (response.status.isSuccess() == false) {
            // TODO: use error logger
            println("An error occurred: ${response.status}, ${response.bodyAsText()}")
        }

        return response.status.isSuccess()
    }
}