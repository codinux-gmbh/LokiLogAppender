package net.codinux.log.loki.web

interface WebClient {

    suspend fun post(url: String, body: Any, contentType: String, headers: Map<String, String> = mapOf()): Boolean

}