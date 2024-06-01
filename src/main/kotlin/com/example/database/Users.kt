package com.example.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

object Users : IntIdTable() {
    val login = varchar("login", 50).uniqueIndex()
    val password = varchar("password", 50)
    val email = varchar("email", 50)
}


class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var login by Users.login
    var password by Users.password
    var email by Users.email
}


class UserService() {
    suspend fun createUser(login: String, password: String, email: String): User = withContext(Dispatchers.IO) {
        transaction {
            User.new {
                this.login = login
                this.password = password
                this.email = email
            }
        }
    }

    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        transaction {
            User.all().toList()
        }
    }

    suspend fun getUserById(id: Int): User? = withContext(Dispatchers.IO) {
        transaction {
            User.findById(id)
        }
    }

    suspend fun getUserByLogin(login: String): User? = withContext(Dispatchers.IO) {
        transaction {
            User.find { Users.login eq login }.singleOrNull()
        }
    }

    suspend fun deleteUser(id: Int): Boolean = withContext(Dispatchers.IO) {
        transaction {
            val user = User.findById(id)
            user?.delete() ?: false
            true
        }
    }
}