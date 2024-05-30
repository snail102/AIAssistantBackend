package com.example.confirmationEmail.models


data class ConfirmEmailRequest(
    val login: String,
    val confirmationCode: String
)