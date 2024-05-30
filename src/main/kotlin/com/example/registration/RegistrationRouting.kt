package com.example.registration

import com.example.authentication.JwtConfig
import com.example.authorization.models.LoginRequest
import com.example.database.UserService
import com.example.localDataSource.TempUserRegistration
import com.example.localDataSource.UserTemp
import com.example.mailSender.MailSender
import com.example.registration.models.RegistrationRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant
import kotlin.random.Random

fun Route.registrationRouting(userService: UserService, mailSender: MailSender) {
    post("/registration") {

        val registrationRequest = call.receive<RegistrationRequest>()
        val user = userService.getUserByLogin(registrationRequest.login)
        if (user == null) {
            val email = registrationRequest.email
            val confirmationCode = generateEmailConfirmationCode()
            TempUserRegistration.addNewUser(
                UserTemp(
                    login = registrationRequest.login,
                    password = registrationRequest.password,
                    email = registrationRequest.email,
                    code = confirmationCode,
                    startRegistrationTimestamp = Instant.now().toEpochMilli()
                )
            )
            print("email $email confirmationCode $confirmationCode")
            mailSender.sendMail(
                toEmail = email,
                subject = "Email confirmation",
                body = confirmationCode
            )
            call.respond("Confirmation code has been sent to your email")
        } else {
            call.respond("User exists")
        }
    }
}


private fun generateEmailConfirmationCode(): String {
    return Random.nextInt(from = 1000, until = 9999).toString()
}