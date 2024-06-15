package com.example.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Chats : IntIdTable() {
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val createdDate = datetime("created_date")
    val lastChangedDate = datetime("last_changed_date")
}


class Chat(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Chat>(Chats)

    var userId by Chats.userId
    var createdDate by Chats.createdDate
    var lastChangedDate by Chats.lastChangedDate
}


class ChatService() {
    suspend fun createChat(
        userId: Int,
        createdDate: LocalDateTime,
        lastChangedDate: LocalDateTime
    ): Chat = withContext(Dispatchers.IO) {
        transaction {
            Chat.new {
                this.userId = userId
                this.createdDate = createdDate
                this.lastChangedDate = lastChangedDate
            }
        }
    }


    suspend fun getAllChatsByUserId(userId: Int) =
        withContext(Dispatchers.IO) {
            Chat.find {  Chats.userId eq userId }.toList()
        }

    suspend fun getChatById(id: Int): Chat? =
        withContext(Dispatchers.IO) {
            transaction {
                Chat.findById(id)
            }
        }
}