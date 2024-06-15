package com.example.chatHistory

import com.example.chatHistory.models.ChatHistoryMessage
import com.example.chatHistory.models.ChatHistoryResponse
import com.example.database.ChatService
import com.example.database.MessageService
import com.example.database.TokenService
import com.example.database.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

private const val CHAT_ID_PARAMETER = "chat_id"

fun Route.chatHistoryRouting(
    tokensService: TokenService,
    chatService: ChatService,
    messageService: MessageService
) {
    get("/chat_history{$CHAT_ID_PARAMETER}") {
        val chatId =
            call.parameters[CHAT_ID_PARAMETER]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.NotFound)

        val tokenHeader = call.request.authorization()?.split(" ") ?: return@get call.respond(HttpStatusCode.NotFound)
        val jwtToken = tokenHeader.getOrNull(1) ?: return@get call.respond(HttpStatusCode.NotFound)

        val token = tokensService.getTokenByAccessToken(jwtToken) ?: return@get call.respond(HttpStatusCode.NotFound)

        val messagesFromChat = messageService.getMessagesByChatId(chatId)

        val chatHistoryResponse = ChatHistoryResponse(
            chatId = chatId,
            messages = messagesFromChat.map { message ->
                ChatHistoryMessage(
                    id = message.id.value,
                    isUserRole = message.isUserRole,
                    content = message.content
                )
            }
        )

        call.respond(chatHistoryResponse)

    }
}
