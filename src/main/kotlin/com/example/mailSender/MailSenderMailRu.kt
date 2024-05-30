package com.example.mailSender

import com.example.utils.loadProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MailSenderMailRu : MailSender {

    private val fromEmail = loadProperties().getProperty("email")
    private val password = loadProperties().getProperty("emailPass")

    override suspend fun sendMail(
        toEmail: String,
        subject: String,
        body: String
    ) {

        val properties = Properties().apply {
            put("mail.smtp.host", "smtp.mail.ru")
            put("mail.smtp.port", "465")
            put("mail.smtp.auth", "true")
            put("mail.smtp.ssl.enable", "true")
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        }

        val session = Session.getInstance(properties, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                return javax.mail.PasswordAuthentication(fromEmail, password)
            }
        })

        withContext(Dispatchers.IO) {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(fromEmail))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                this.subject = subject
                setText(body)
            }
            Transport.send(message)
        }
    }
}