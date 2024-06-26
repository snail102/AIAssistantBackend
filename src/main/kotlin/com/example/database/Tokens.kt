package com.example.database

import com.example.authentication.GeneratedTokens
import com.example.database.Tokens.refreshToken
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
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Tokens : IntIdTable() {
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val accessToken = varchar("access_token", 300)
    val refreshToken = varchar("refresh_token", 300)
    val accessExpiresAt = datetime("access_expires_at")
    val refreshExpiresAt = datetime("refresh_expires_at")
}


class TokenEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TokenEntity>(Tokens)

    var userId by Tokens.userId
    var accessToken by Tokens.accessToken
    var refreshToken by Tokens.refreshToken
    var accessExpiresAt by Tokens.accessExpiresAt
    var refreshExpiresAt by Tokens.refreshExpiresAt
}


class TokenService() {
    suspend fun createToken(
        userId: Int,
        accessToken: String,
        refreshToken: String,
        accessExpiresAt: LocalDateTime,
        refreshExpiresAt: LocalDateTime
    ): TokenEntity = withContext(Dispatchers.IO) {
        transaction {
            TokenEntity.new {
                this.userId = userId
                this.accessToken = accessToken
                this.refreshToken = refreshToken
                this.accessExpiresAt = accessExpiresAt
                this.refreshExpiresAt = refreshExpiresAt
            }
        }

    }

    suspend fun getTokenByAccessToken(accessToken: String): TokenEntity? =
        withContext(Dispatchers.IO) {
            transaction {
                TokenEntity.find { Tokens.accessToken eq accessToken }.singleOrNull()
            }
        }

    suspend fun getTokenByRefreshToken(refreshToken: String): TokenEntity? =
        withContext(Dispatchers.IO) {
            transaction {
                TokenEntity.find { Tokens.refreshToken eq refreshToken }.singleOrNull()
            }
        }

    suspend fun updateToken(generatedTokens: GeneratedTokens) {
        return withContext(Dispatchers.IO) {
            transaction {
                Tokens.update({ Tokens.userId eq generatedTokens.userId }) {
                    it[accessToken] = generatedTokens.access
                    it[refreshToken] = generatedTokens.refresh
                    it[accessExpiresAt] = generatedTokens.accessExpiresAt
                    it[refreshExpiresAt] = generatedTokens.refreshExpiresAt
                }
            }
        }
    }

    suspend fun deleteTokenByUserId(userId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            transaction {
                val user = TokenEntity.find { Tokens.userId eq userId }.singleOrNull()
                user?.delete() ?: false
                true
            }
        }
    }
}