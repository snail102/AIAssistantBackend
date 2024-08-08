package com.example.confirmationEmail

import com.example.authentication.JwtConfig
import com.example.confirmationEmail.models.ConfirmEmailRequest
import com.example.database.GptTokenService
import com.example.database.TokenService
import com.example.database.UserService
import com.example.localDataSource.TempUserRegistration
import com.example.models.TokenDto
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.confirmationEmailRouting(
    userService: UserService,
    gptTokenService: GptTokenService,
    tokenService: TokenService
) {
    post("/confirmation_email") {

        val confirmEmailRequest = call.receive<ConfirmEmailRequest>()
        val userTemp = TempUserRegistration.getUserByLogin(confirmEmailRequest.login)
            ?: return@post call.respond("Not found registration user")

        if (userTemp.code == confirmEmailRequest.confirmationCode) {

            val user = userService.createUser(
                login = userTemp.login,
                password = userTemp.password,
                email = userTemp.email
            )

            val gptTokens = gptTokenService.createGptToken(
                userId = user.id.value,
                availableGtpTokens = 0,
                usedGptTokens = 0
            )

            val generatedTokens = JwtConfig.getTokens(user.id.value)

            tokenService.createToken(
                userId = user.id.value,
                accessToken = generatedTokens.access,
                refreshToken = generatedTokens.refresh,
                accessExpiresAt = generatedTokens.accessExpiresAt,
                refreshExpiresAt = generatedTokens.refreshExpiresAt
            )
            call.respond(
                TokenDto(
                    accessToken = generatedTokens.access,
                    refreshToken = generatedTokens.refresh
                )
            )
        } else {
            call.respond("Code not valid")
        }
    }
}
