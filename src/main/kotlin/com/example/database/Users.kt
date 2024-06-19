package com.example.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.transactions.transaction

object Users : IntIdTable() {
    val login = varchar("login", 50).uniqueIndex()
    val password = varchar("password", 50)
    val email = varchar("email", 50)
}


class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var login by Users.login
    var password by Users.password
    var email by Users.email
}


class UserService() {
    suspend fun createUser(login: String, password: String, email: String): UserEntity = withContext(Dispatchers.IO) {
        transaction {
            UserEntity.new {
                this.login = login
                this.password = password
                this.email = email
            }
        }
    }

    suspend fun getAllUsers(): List<UserEntity> = withContext(Dispatchers.IO) {
        transaction {
            UserEntity.all().toList()
        }
    }

    suspend fun getUserById(id: Int): UserEntity? = withContext(Dispatchers.IO) {
        transaction {
            UserEntity.findById(id)
        }
    }

    suspend fun getUserByLogin(login: String): UserEntity? = withContext(Dispatchers.IO) {
        transaction {
            UserEntity.find { Users.login.lowerCase() eq login.lowercase() }.singleOrNull()
        }
    }

    suspend fun deleteUser(id: Int): Boolean = withContext(Dispatchers.IO) {
        transaction {
            val userEntity = UserEntity.findById(id)
            userEntity?.delete() ?: false
            true
        }
    }
}