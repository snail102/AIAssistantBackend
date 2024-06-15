package com.example.confirmationEmail.models

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmEmailRequest(
    val login: String,
    val confirmationCode: String
)