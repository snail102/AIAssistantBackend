package com.example.confirmationEmail.models

import kotlinx.serialization.Serializable

@Serializable
data class RetryCodeRequest(
    val login: String
)