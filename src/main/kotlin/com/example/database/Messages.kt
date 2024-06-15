package com.example.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

object Messages : IntIdTable() {
    val chatId = integer("chat_id").references(Chats.id, onDelete = ReferenceOption.CASCADE)
    val isUserRole = bool("is_user_role")
    val content = varchar("content", length = 5000)
    val dateTime = datetime("date_time")
}


class Message(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Message>(Messages)

    var chatId by Messages.chatId
    var isUserRole by Messages.isUserRole
    var content by Messages.content
    var dateTime by Messages.dateTime
}


class MessageService() {

    suspend fun createMessage(
        chatId: Int,
        isUserRole: Boolean,
        content: String,
        dateTime: LocalDateTime
    ): Message = withContext(Dispatchers.IO) {
        transaction {
            Message.new {
                this.chatId = chatId
                this.isUserRole = isUserRole
                this.content = content
                this.dateTime = dateTime
            }
        }
    }

    suspend fun getMessagesByChatId(chatId: Int) =
        withContext(Dispatchers.IO) {
            transaction {
                Message.find { Messages.chatId eq chatId }.toList()
            }
        }

}