package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class RequestGpt(
    val model: String,
    val messages: List<MessageGpt>
)
