package com.example.confirmationEmail

import com.example.authentication.JwtConfig
import com.example.confirmationEmail.models.ConfirmEmailRequest
import com.example.database.UserService
import com.example.localDataSource.TempUserRegistration
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.confirmationEmailRouting(userService: UserService) {
    post("/confirmation_email") {

        val confirmEmailRequest = call.receive<ConfirmEmailRequest>()
        val userTemp = TempUserRegistration.getUserByLogin(confirmEmailRequest.login) ?: return@post call.respond("Not found registration user")

        if (userTemp.code == confirmEmailRequest.confirmationCode) {

            val user = userService.createUser(
                login = userTemp.login,
                password = userTemp.password,
                email = userTemp.email
            )

            val accessToken = JwtConfig.generateAccessToken(user.id.toString())
            val refreshToken = JwtConfig.generateRefreshToken(user.id.toString())
            call.respond(mapOf("accessToken" to accessToken, "refreshToken" to refreshToken))
        } else {
            call.respond("Code not valid")
        }
    }
}
