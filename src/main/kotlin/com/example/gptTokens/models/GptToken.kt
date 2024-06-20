package com.example.gptTokens.models

data class GptToken(
    val id: Int,
    val userId: Int,
    val availableGtpTokens: Int,
    val usedGptTokens: Int
)