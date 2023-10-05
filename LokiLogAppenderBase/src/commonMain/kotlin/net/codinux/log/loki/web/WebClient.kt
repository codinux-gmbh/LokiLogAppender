package net.codinux.log.loki.web

interface WebClient {

    suspend fun post(body: Any): Boolean

}