package ca.dialai.crud.test

import kotlinx.serialization.Serializable

/**
 * duplicate of `TodoRecord` found in the server module.
 */
@Serializable
data class TodoRecord(
    val content: String,
    val isComplete: Boolean
)