package net.codinux.log.loki.web

import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class KtorWebClient(lokiPushApiUrl: String) : WebClient {

    private val client = HttpClient {
        defaultRequest {
            url(lokiPushApiUrl)
        }
    }

    override suspend fun post(url: String, body: Any, contentType: String, headers: Map<String, String>): Boolean {
        val response = client.request {
            this.method = HttpMethod.Post
            contentType(ContentType.parse(contentType))
            setBody(body)

            headers.forEach { (name, value) -> this.headers.append(name, value) }
        }

        if (response.status.isSuccess() == false) {
            // TODO: use error logger
            println("An error occurred: ${response.status}, ${response.bodyAsText()}")
        }

        return response.status.isSuccess()
    }
}