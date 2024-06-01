package com.example.plugins

import com.example.authorization.authorizationRouting
import com.example.client
import com.example.confirmationEmail.confirmationEmailRouting
import com.example.database.TokenService
import com.example.database.UserService
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
    val url = "https://api.openai.com/v1/chat/completions"

    routing {
        post("/chat/completions") {
            val user = call.request.headers["User"]
            if (user != "Admin") {
                call.respond(HttpStatusCode.Forbidden)
            }
            val chatMessage = call.receive<ChatMessage>()
            val startDialog = listOf(
                MessageGpt(
                    role = "system",
                    content = "You are a programming assistant"
                ),
                MessageGpt(
                    role = "user",
                    content = chatMessage.content
                )
            )

            val continuationDialog = ChatHistory.getPreviousMessages() + MessageGpt(
                role = "user",
                content = chatMessage.content
            )
            val request = RequestGpt(
                model = "gpt-3.5-turbo",
                messages = if (ChatHistory.getPreviousMessages().isEmpty()) {
                    startDialog
                } else {
                    continuationDialog
                }
            )
            val response: HttpResponse = client.post(url) {
                setBody(request)
            }
            val responseGpt = response.body<ResponseGpt>()

            if (response.status == HttpStatusCode.OK) {
                val savedMessages = if (ChatHistory.getPreviousMessages().isEmpty()) {
                    startDialog
                } else {
                    continuationDialog
                }
                ChatHistory.addAll(savedMessages)
            }

            call.respond(responseGpt)
        }


        val userService by inject<UserService>()
        authorizationRouting(
            userService = userService
        )

        val mailSender by inject<MailSender>()
        registrationRouting(
            userService = userService,
            mailSender = mailSender
        )

        confirmationEmailRouting(
            userService = userService
        )

        val tokenService by inject<TokenService>()
        refreshTokenRouting(tokenService = tokenService)

        authenticate("auth-jwt") {
            get("/protected") {
                call.respondText("This is a protected route")
            }
        }
    }
}
