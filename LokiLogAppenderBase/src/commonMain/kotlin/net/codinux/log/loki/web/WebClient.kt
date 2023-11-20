package net.codinux.log.loki.web

interface WebClient {

    suspend fun post(body: Any, logError: Boolean = false): Int

}