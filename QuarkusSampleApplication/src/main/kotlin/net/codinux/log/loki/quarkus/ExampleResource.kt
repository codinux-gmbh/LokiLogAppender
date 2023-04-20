package net.codinux.log.loki.quarkus

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
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