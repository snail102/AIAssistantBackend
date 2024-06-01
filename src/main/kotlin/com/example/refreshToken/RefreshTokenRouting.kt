package com.example.refreshToken

import com.example.authentication.JwtConfig
import com.example.database.TokenService
import com.example.models.TokenDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.refreshTokenRouting(tokenService: TokenService) {
    post("/refresh") {
        val refreshTokenRequest = call.receive<RefreshTokenRequest>()
        val token = tokenService.getTokenByRefreshToken(
            refreshToken = refreshTokenRequest.refreshToken
        ) ?: return@post call.respond(HttpStatusCode.NotFound)

        val generatedTokens = JwtConfig.getTokens(token.userId)
        tokenService.updateToken(generatedTokens)
        call.respond(
            TokenDto(
                accessToken = generatedTokens.access,
                refreshToken = generatedTokens.refresh
            )
        )
    }
}