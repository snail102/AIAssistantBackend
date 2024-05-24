package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnswerGpt(
    val index: Int,
    val message: MessageGpt,
    @SerialName("finish_reason")
    val finishReason: String
)