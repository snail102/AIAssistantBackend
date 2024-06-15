package com.example.chatAll

import com.example.chatAll.models.ChatAllResponse
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


fun Route.chatAllRouting(
    tokensService: TokenService,
    userService: UserService,
    chatService: ChatService,
    messageService: MessageService
) {
    get("/chat_all") {

        val tokenHeader = call.request.authorization()?.split(" ") ?: return@get call.respond(HttpStatusCode.NotFound)
        val jwtToken = tokenHeader.getOrNull(1) ?: return@get call.respond(HttpStatusCode.NotFound)

        val token = tokensService.getTokenByAccessToken(jwtToken) ?: return@get call.respond(HttpStatusCode.NotFound)

        val chats = chatService.getAllChatsByUserId(token.userId)

        val chatResponseList = chats.map { chat->
            ChatAllResponse(
                chatId = chat.id.value,
                firstMessagePreview = messageService.getMessagesByChatId(chat.id.value).firstOrNull()?.content.orEmpty(),
                createdDate = chat.createdDate.toInstant(TimeZone.UTC).toEpochMilliseconds(),
                lastChangedDate = chat.lastChangedDate.toInstant(TimeZone.UTC).toEpochMilliseconds(),
            )
        }

        call.respond(chatResponseList)
    }

}