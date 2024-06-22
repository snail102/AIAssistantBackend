package com.example.plugins

import com.example.authorization.authorizationRouting
import com.example.chat.chatRouting
import com.example.chatAll.chatAllRouting
import com.example.chatHistory.chatHistoryRouting
import com.example.client
import com.example.confirmationEmail.confirmationEmailRouting
import com.example.database.*
import com.example.gptTokens.gptTokensRouting
import com.example.localDataSource.ChatHistory
import com.example.mailSender.MailSender
import com.example.models.ChatMessage
import com.example.models.MessageGpt
import com.example.models.RequestGpt
import com.example.models.ResponseGpt
import com.example.refreshToken.refreshTokenRouting
import com.example.registration.registrationRouting
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.configureRouting() {
    routing {

        val userService by inject<UserService>()
        val tokenService by inject<TokenService>()
        val chatService by inject<ChatService>()
        val messageService by inject<MessageService>()
        val gptTokenService by inject<GptTokenService>()

        authorizationRouting(
            userService = userService,
            tokenService = tokenService
        )

        val mailSender by inject<MailSender>()
        registrationRouting(
            userService = userService,
            mailSender = mailSender
        )

        confirmationEmailRouting(
            userService = userService,
            gptTokenService = gptTokenService
        )

        refreshTokenRouting(tokenService = tokenService)

        authenticate("auth-jwt") {
            get("/protected") {
                call.respondText("This is a protected route")
            }

            chatRouting(
                tokensService = tokenService,
                userService = userService,
                chatService = chatService,
                messageService = messageService,
                gptTokenService = gptTokenService
            )

            chatHistoryRouting(
                tokensService = tokenService,
                chatService = chatService,
                messageService = messageService
            )

            chatAllRouting(
                tokensService = tokenService,
                userService = userService,
                chatService = chatService,
                messageService = messageService
            )

            gptTokensRouting(
                gptTokenService = gptTokenService,
                tokensService = tokenService
            )
        }
    }
}
