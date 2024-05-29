package com.example.authorization

import com.example.authentication.JwtConfig
import com.example.authorization.models.LoginRequest
import com.example.database.UserService
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
            val accessToken = JwtConfig.generateAccessToken(user.id.toString())
            val refreshToken = JwtConfig.generateRefreshToken(user.id.toString())
            call.respond(mapOf("accessToken" to accessToken, "refreshToken" to refreshToken))
        }
    }
}