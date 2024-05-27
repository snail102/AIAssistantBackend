package com.example.plugins

import com.example.buildCurlCommand
import com.example.client
import com.example.localDataSource.ChatHistory
import com.example.models.ChatMessage
import com.example.models.MessageGpt
import com.example.models.RequestGpt
import com.example.models.ResponseGpt
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


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
    }
}
