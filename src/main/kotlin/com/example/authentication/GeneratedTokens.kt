package com.example.authentication

import kotlinx.datetime.LocalDateTime

data class GeneratedTokens(
    val userId: Int,
    val access: String,
    val accessExpiresAt: LocalDateTime,
    val refresh: String,
    val refreshExpiresAt: LocalDateTime
)