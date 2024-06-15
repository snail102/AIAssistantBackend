package com.example.chatAll.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatAllResponse(
    val chatId: Int,
    val firstMessagePreview: String,
    val createdDate: Long,
    val lastChangedDate: Long
)