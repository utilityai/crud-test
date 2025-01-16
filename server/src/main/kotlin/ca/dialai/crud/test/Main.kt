package ca.dialai.crud.test

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail
import io.ktor.util.collections.ConcurrentMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles
import java.util.concurrent.atomic.AtomicLong

/**
 * Logger to be used for all server operations.
 */
private val logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

/**
 * a record in our in-memory database that represents a to-do item.
 */
@Serializable
data class TodoRecord(
    val content: String,
    val isComplete: Boolean
)

/**
 * Our in-memory database, the keys are longs created by [idCounter] (which should only count up)
 */
val database = ConcurrentMap<Long, TodoRecord>()

/**
 * How we generate unique IDs for [database], should only ever be incremented.
 */
val idCounter = AtomicLong(0)

fun main() {
    embeddedServer(Netty, configure = {
        // Listen on localhost http://0.0.0.0:8080
        connector {
            port = 8080
            host = "0.0.0.0"
        }
    }) {
        // accept and send json
        install(ContentNegotiation) {
            json()
        }
        // log all calls
        install(CallLogging) {
            logger = ca.dialai.crud.test.logger
        }

        routing {
            // Create a new to-do
            post {
                // receive the HTTP body and parse into a TodoRecord
                val record = call.receive<TodoRecord>()
                // increment and get the new ID for the to-do
                val id = idCounter.incrementAndGet()
                // add the to-do to the database
                database[id] = record
                logger.info("successfully created to-do {}", id)
                // respond and give the client the new to-do's ID
                call.respond(HttpStatusCode.OK, buildJsonObject { put("id", id) })
            }
            // get an existing to-do by ID
            get("{id}") {
                // get the ID from the path
                val id = call.pathParameters.getOrFail("id").toLong()
                // try to get it from the database
                when (val record = database[id]) {
                    // if it does not exist, return not found
                    null -> {
                        logger.info("Unable to get to-do {} as it was not found", id)
                        call.respond(HttpStatusCode.NotFound, buildJsonObject { put("error", "not found") })
                    }

                    // if it exists, return the to-do
                    else -> {
                        logger.info("successfully got to-do {}", id)
                        call.respond(HttpStatusCode.OK, record)
                    }
                }
            }
            // Delete an existing to-do by id
            delete("{id}") {
                // get the ID from the path
                val id = call.pathParameters.getOrFail("id").toLong()
                // try to remove the to-do from the database
                when (database.remove(id)) {
                    // if it does not exist, return not found
                    null -> {
                        logger.info("Failed to delete to-do {} as it was not found", id)
                        call.respond(HttpStatusCode.NotFound, buildJsonObject { put("error", "not found") })
                    }

                    // if it exists, return a success message
                    else -> {
                        logger.info("successfully deleted to-do {}", id)
                        call.respond(HttpStatusCode.OK, buildJsonObject { put("success", true) })
                    }
                }
            }
        }
    }.start(wait = true)
}