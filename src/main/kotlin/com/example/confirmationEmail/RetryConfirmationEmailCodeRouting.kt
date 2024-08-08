package com.example.confirmationEmail

import com.example.confirmationEmail.models.RetryCodeRequest
import com.example.localDataSource.TempUserRegistration
import com.example.localDataSource.UserTemp
import com.example.mailSender.MailSender
import com.example.registration.generateEmailConfirmationCode
import com.example.registration.models.RegistrationRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant

fun Route.retryConfirmationEmailCodeRouting(mailSender: MailSender) {

    post("/retry_confirmation_email") {
        val retryCodeRequest = call.receive<RetryCodeRequest>()


        val confirmationCode = generateEmailConfirmationCode()
        val userTemp = TempUserRegistration.replaceCode(
            login = retryCodeRequest.login,
            newCode = confirmationCode
        )

        if (userTemp != null) {
            mailSender.sendMail(
                toEmail = userTemp.email,
                subject = "Email confirmation",
                body = confirmationCode
            )
            print("retryCode confirmationCode $confirmationCode")
            call.respond(HttpStatusCode.OK)
        } else {
            print("error retry code")
            call.respond(HttpStatusCode.NotFound)
        }
    }


}