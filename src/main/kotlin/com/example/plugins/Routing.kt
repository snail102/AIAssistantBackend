package com.example.plugins

import com.example.buildCurlCommand
import com.example.client
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
            val chatMessage = call.receive<ChatMessage>()
            val request = RequestGpt(
                model = "gpt-3.5-turbo",
                messages = listOf(
                    MessageGpt(
                        role = "system",
                        content = "You are a programming assistant"
                    ),
                    MessageGpt(
                        role = "user",
                        content = chatMessage.content
                    )
                )
            )
            val response: HttpResponse = client.post(url) {
                setBody(request)
            }
            val responseGpt = response.body<ResponseGpt>()

            call.respond(responseGpt)
        }
    }
}
