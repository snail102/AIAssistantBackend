package com.example.chat.domain.models

import kotlinx.datetime.LocalDateTime

data class Chat(
    val id: Int,
    val userId: Int,
    val createdDate: LocalDateTime,
    val lastChangedDate: LocalDateTime
)