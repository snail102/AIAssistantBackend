package com.example.database

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
    fun createUser(login: String, password: String, email: String): User = transaction {
        User.new {
            this.login = login
            this.password = password
            this.email = email
        }
    }

    fun getAllUsers(): List<User> = transaction {
        User.all().toList()
    }

    fun getUserById(id: Int): User? = transaction {
        User.findById(id)
    }

    fun getUserByLogin(login: String): User? = transaction {
        User.find { Users.login eq login }.singleOrNull()
    }

    fun deleteUser(id: Int): Boolean = transaction {
        val user = User.findById(id)
        user?.delete() ?: false
        true
    }
}