package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGpt(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<AnswerGpt>,
    val usage: InfoUsageTokensGpt
)