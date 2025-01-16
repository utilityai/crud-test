package ca.dialai.crud.test

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

private val logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

fun main(): Unit = runBlocking {
    val client = TodoClient()

    val doesNotExistYet = client.get(-1)
    if (doesNotExistYet != null) {
        logger.error("-1 should never exist!")
    } else {
        logger.info("as expected, received null from server for -1")
    }

    val todoRecord = TodoRecord("complete a coding test", false)
    val newId = client.create(todoRecord)
    logger.info("created to-do {}", newId)

    val fetched = client.get(newId)

    if (fetched == null) {
        logger.error("{} is null despite us just creating it", newId)
    } else if (fetched != todoRecord) {
        logger.error("to-do {} does not match what we inserted", newId)
    } else {
        logger.info("created and fetched to-do {} as expected", newId)
    }

    val success = client.delete(newId)
    if (!success) {
        logger.error("delete failed for id {}", newId)
    } else {
        logger.info("delete success for id {}", newId)
    }

    val deletedRecord = client.get(newId)

    if (deletedRecord != null) {
        logger.error("delete failed for id {} - to-do still exists!", newId)
    } else {
        logger.info("as expected, was unable to get to-do {} after deletion", newId)
    }
}