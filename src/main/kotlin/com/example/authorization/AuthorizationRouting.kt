package com.example.authorization

import com.example.authentication.JwtConfig
import com.example.authorization.models.LoginRequest
import com.example.database.UserService
import com.example.models.TokenDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.authorizationRouting(userService: UserService) {
    post("/login") {

        val loginRequest = call.receive<LoginRequest>()
        val user = userService.getUserByLogin(loginRequest.login)
        if (user == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            val generatedTokens = JwtConfig.getTokens(user.id.value)
            call.respond(
                TokenDto(
                    accessToken = generatedTokens.access,
                    refreshToken = generatedTokens.refresh
                )
            )
        }
    }
}