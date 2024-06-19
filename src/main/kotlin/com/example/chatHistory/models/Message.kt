package com.example.chatHistory.models

data class Message(
    val id: Int,
    val isUserRole: Boolean,
    val content: String
)
