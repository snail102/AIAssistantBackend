package com.example.mailSender

interface MailSender {

    suspend fun sendMail(
        toEmail: String,
        subject: String,
        body: String
    )
}