package net.codinux.log.loki.quarkus

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import net.codinux.log.LoggerFactory.logger

@Path("/hello")
class ExampleResource {

    private val log by logger()

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello(@QueryParam("query") stringToReverse: String): String {
        log.info { "Received query '$stringToReverse'" }

        return stringToReverse.reversed()
    }
}