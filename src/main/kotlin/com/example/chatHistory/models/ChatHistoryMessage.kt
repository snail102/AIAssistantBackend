package com.example.chatHistory.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatHistoryMessage(
    val id: Int,
    val isUserRole: Boolean,
    val content: String
)