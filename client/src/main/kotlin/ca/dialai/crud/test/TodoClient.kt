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

class TodoClient(
    client: HttpClient = HttpClient(),
    serverUrl: Url = Url("http://localhost:8080")
) {
    private val logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    /**
     * configure the http client
     */
    private val httpClient = client.config {
        install(ContentNegotiation) {
            json()
        }
        install(Logging) {
            level = LogLevel.BODY
            logger = object : Logger {
                override fun log(message: String) {
                    this@TodoClient.logger.debug(message)
                }
            }
        }
        defaultRequest {
            url { takeFrom(serverUrl) }
        }
    }

    /**
     * Get a to-do record by its ID
     */
    suspend fun get(id: Long): TodoRecord? {
        val response = httpClient.get {
            url {
                appendPathSegments(id.toString())
            }
        }

        return if (response.status != HttpStatusCode.OK) {
            null
        } else {
            response.body<TodoRecord>()
        }
    }

    /**
     * Create a to-do record
     */
    suspend fun create(todoRecord: TodoRecord): Long {
        val response = httpClient.post {
            contentType(ContentType.Application.Json)
            setBody(todoRecord)
        }

        return response.body<JsonObject>()["id"]!!.jsonPrimitive.content.toLong()
    }

    /**
     * Delete a to-do record
     */
    suspend fun delete(id: Long): Boolean {
        val response = httpClient.delete {
            url {
                appendPathSegments(id.toString())
            }
        }

        return response.status == HttpStatusCode.OK
    }
}