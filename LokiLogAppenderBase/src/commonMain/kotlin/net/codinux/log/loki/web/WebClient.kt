package net.codinux.log.loki.web

interface WebClient {

    /**
     * The logic is: The HttpStatusCode is always returns, the response body only in case of error.
     */
    suspend fun post(body: Any, logError: Boolean = false): Pair<Int, String?>

}