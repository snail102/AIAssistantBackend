package com.example.registration.models

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val login: String,
    val password: String,
    val email: String
)