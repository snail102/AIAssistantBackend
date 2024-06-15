package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val chatId: Int,
    val content: String
)