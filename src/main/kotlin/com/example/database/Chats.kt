package com.example.database

import com.example.chat.domain.models.Chat
import com.example.utils.suspendTransaction
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

object Chats : IntIdTable() {
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val createdDate = datetime("created_date")
    val lastChangedDate = datetime("last_changed_date")
}


class ChatEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ChatEntity>(Chats)

    var userId by Chats.userId
    var createdDate by Chats.createdDate
    var lastChangedDate by Chats.lastChangedDate
}

private fun daoToModel(dao: ChatEntity) = Chat(
    id = dao.id.value,
    userId = dao.userId,
    createdDate = dao.createdDate,
    lastChangedDate = dao.lastChangedDate
)


class ChatService() {
    suspend fun createChat(
        userId: Int,
        createdDate: LocalDateTime,
        lastChangedDate: LocalDateTime
    ): ChatEntity = withContext(Dispatchers.IO) {
        transaction {
            ChatEntity.new {
                this.userId = userId
                this.createdDate = createdDate
                this.lastChangedDate = lastChangedDate
            }
        }
    }


    suspend fun getAllChatsByUserId(userId: Int) = suspendTransaction {
        ChatEntity.find { Chats.userId eq userId }.map(::daoToModel)
    }

    suspend fun getChatById(id: Int): ChatEntity? =
        withContext(Dispatchers.IO) {
            transaction {
                ChatEntity.findById(id)
            }
        }
}