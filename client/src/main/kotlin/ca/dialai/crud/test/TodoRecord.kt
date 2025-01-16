package ca.dialai.crud.test

import kotlinx.serialization.Serializable

@Serializable
data class TodoRecord(
    val content: String,
    val isComplete: Boolean
)