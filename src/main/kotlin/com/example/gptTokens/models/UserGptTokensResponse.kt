package com.example.gptTokens.models

import kotlinx.serialization.Serializable

@Serializable
data class UserGptTokensResponse(
    val availableGptTokens: Int,
    val usedGptTokens: Int
)