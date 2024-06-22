package com.example.gptTokens

import com.example.database.GptTokenService
import com.example.database.TokenService
import com.example.gptTokens.models.UserGptTokensResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.gptTokensRouting(
    gptTokenService: GptTokenService,
    tokensService: TokenService,
) {
    get("/user_gpt_tokens") {
        val tokenHeader = call.request.authorization()?.split(" ") ?: return@get call.respond(HttpStatusCode.NotFound)
        val jwtToken = tokenHeader.getOrNull(1) ?: return@get call.respond(HttpStatusCode.NotFound)

        val token = tokensService.getTokenByAccessToken(jwtToken) ?: return@get call.respond(HttpStatusCode.Unauthorized)

        val gptTokens = gptTokenService.getGtpTokensByUserId(userId = token.userId) ?: return@get call.respond(
            HttpStatusCode.NotFound
        )

        val userGptTokensResponse = UserGptTokensResponse(
            availableGptTokens = gptTokens.availableGtpTokens,
            usedGptTokens = gptTokens.usedGptTokens
        )

        call.respond(userGptTokensResponse)

    }
}