package com.example.database

import com.example.gptTokens.models.GptToken
import com.example.utils.suspendTransaction
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object GptTokens : IntIdTable() {
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val availableGtpTokens = integer("available_gtp_tokens")
    val usedGptTokens = integer("used_gpt_tokens")
}


class GptTokenEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GptTokenEntity>(GptTokens)

    var userId by GptTokens.userId
    var availableGtpTokens by GptTokens.availableGtpTokens
    var usedGptTokens by GptTokens.usedGptTokens
}

private fun entityToDomain(entity: GptTokenEntity) = GptToken(
    id = entity.id.value,
    userId = entity.userId,
    availableGtpTokens = entity.availableGtpTokens,
    usedGptTokens = entity.usedGptTokens
)


class GptTokenService() {

    suspend fun createGptToken(userId: Int, availableGtpTokens: Int, usedGptTokens: Int) = suspendTransaction {
        GptTokenEntity.new {
            this.userId = userId
            this.availableGtpTokens = availableGtpTokens
            this.usedGptTokens = usedGptTokens
        }
    }

    suspend fun getGtpTokensByUserId(userId: Int) = suspendTransaction {
        GptTokenEntity.find { GptTokens.userId eq userId }.singleOrNull()?.let(::entityToDomain)
    }

    suspend fun getGptTokensById(id: Int) = suspendTransaction {
        GptTokenEntity.findById(id)?.let(::entityToDomain)
    }

}