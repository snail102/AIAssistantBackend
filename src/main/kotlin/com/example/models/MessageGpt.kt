package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class MessageGpt(
    val role: String,
    val content: String
)