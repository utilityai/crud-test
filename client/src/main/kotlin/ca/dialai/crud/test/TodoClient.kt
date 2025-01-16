package ca.dialai.crud.test

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

/**
 * An HTTP client for interacting with the to-do server
 */
class TodoClient(
    client: HttpClient = HttpClient(),
    serverUrl: Url = Url("http://localhost:8080")
) {
    private val logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    /**
     * configure the http client
     */
    private val httpClient = client.config {
        // allow sending and receiving json
        install(ContentNegotiation) {
            json()
        }
        // log requests and responses
        install(Logging) {
            level = LogLevel.BODY
            logger = object : Logger {
                override fun log(message: String) {
                    this@TodoClient.logger.debug(message)
                }
            }
        }
        // always use the [serverUrl]
        defaultRequest {
            url { takeFrom(serverUrl) }
        }
    }

    /**
     * Get a to-do record by its ID
     */
    suspend fun get(id: Long): TodoRecord? {
        // send a get request with the ID as part of the path
        val response = httpClient.get {
            url {
                appendPathSegments(id.toString())
            }
        }

        // check if it was okay (assume `NotFound` if not `Ok`)
        return if (response.status != HttpStatusCode.OK) {
            // return null if not Ok
            null
        } else {
            // return the body parsed into a `TodoRecord` if `Ok`
            response.body<TodoRecord>()
        }
    }

    /**
     * Create a to-do record
     */
    suspend fun create(todoRecord: TodoRecord): Long {
        // send a post request in json format with the body as the todoRecord
        val response = httpClient.post {
            contentType(ContentType.Application.Json)
            setBody(todoRecord)
        }

        // get the ID from the response (assumes always successful)
        return response.body<JsonObject>()["id"]!!.jsonPrimitive.content.toLong()
    }

    /**
     * Delete a to-do record
     */
    suspend fun delete(id: Long): Boolean {
        // send a delete request, appending the id to the HTTP request's url
        val response = httpClient.delete {
            url {
                appendPathSegments(id.toString())
            }
        }

        // return `true` if successful, false otherwise.
        return response.status == HttpStatusCode.OK
    }
}