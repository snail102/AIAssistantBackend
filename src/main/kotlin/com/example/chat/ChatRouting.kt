package com.example.chat

import com.example.client
import com.example.database.*
import com.example.localDataSource.ChatHistory
import com.example.models.ChatMessage
import com.example.models.MessageGpt
import com.example.models.RequestGpt
import com.example.models.ResponseGpt
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.*
import java.time.ZoneOffset

private const val URL = "https://api.openai.com/v1/chat/completions"

fun Route.chatRouting(
    tokensService: TokenService,
    userService: UserService,
    chatService: ChatService,
    messageService: MessageService,
    gptTokenService: GptTokenService
) {
    post("/chat/completions") {

        val tokenHeader = call.request.authorization()?.split(" ") ?: return@post call.respond(HttpStatusCode.NotFound)
        val jwtToken = tokenHeader.getOrNull(1) ?: return@post call.respond(HttpStatusCode.NotFound)

        val token = tokensService.getTokenByAccessToken(jwtToken) ?: return@post call.respond(HttpStatusCode.Unauthorized)

        val chatMessage = call.receive<ChatMessage>()
        val startDialog = listOf(
            MessageGpt(
                role = "system",
                content = "You are a assistant"
            ),
            MessageGpt(
                role = "user",
                content = chatMessage.content
            )
        )
        val chatId = chatMessage.chatId
        val historyMessage: List<MessageGpt>
        val date = java.time.LocalDateTime.now().toKotlinLocalDateTime()
        if (chatId != 0) {
            val chat = chatService.getChatById(chatId)

            if (chat != null && chat.userId == token.userId) {
                val messages = messageService.getMessagesByChatId(chat.id.value)
                historyMessage = messages.map {
                    MessageGpt(
                        role = if (it.isUserRole) "user" else "assistant",
                        content = it.content
                    )
                }
            } else {
                historyMessage = emptyList()
            }
        } else {
            historyMessage = emptyList()
        }

        val continuationDialog = historyMessage + MessageGpt(
            role = "user",
            content = chatMessage.content
        )
        val request = RequestGpt(
            model = "gpt-3.5-turbo",
            messages = if (historyMessage.isEmpty()) {
                startDialog
            } else {
                continuationDialog
            }
        )
        val response: HttpResponse = client.post(URL) {
            setBody(request)
        }
        val responseGpt = response.body<ResponseGpt>()

        if (response.status == HttpStatusCode.OK) {

            val chatIdForSave = if (chatId == 0) {
                chatService.createChat(
                    userId = token.userId,
                    createdDate = date,
                    lastChangedDate = date
                ).id.value
            } else {
                chatId
            }

            gptTokenService.updateGptTokensByUserId(userId = token.userId, usedGptTokens = responseGpt.usage.totalTokens)


            messageService.createMessage(
                chatId = chatIdForSave,
                isUserRole = true,
                content = chatMessage.content,
                dateTime = date
            )

            val responseChatMessage = ChatMessage(
                chatId = chatIdForSave,
                content = responseGpt.choices.firstOrNull()?.message?.content.orEmpty()
            )

            messageService.createMessage(
                chatId = chatIdForSave,
                isUserRole = false,
                content = responseChatMessage.content,
                dateTime = date
            )

            call.respond(responseChatMessage)
        }

        call.respond(response.status)
    }
}