package com.example.chatHistory.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatHistoryResponse(
    val chatId: Int,
    val messages: List<ChatHistoryMessage>
)