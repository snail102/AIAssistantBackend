package com.example.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object Users : IntIdTable() {
    val name = varchar("name", 50)
}


data class User(val id: Int, val name: String)



class UserService {
    fun getAllUsers(): List<User> = transaction {
        Users.selectAll().map {
            User(
                it[Users.id].value,
                it[Users.name],
            )
        }
    }

    fun getUserById(id: Int): User? = transaction {
        Users.selectAll().where { Users.id eq id }
            .map {
                User(
                    it[Users.id].value,
                    it[Users.name],
                )
            }.singleOrNull()
    }

    fun addUser(name: String, age: Int): User = transaction {
        val id = Users.insertAndGetId {
            it[Users.name] = name
        }.value
        User(id, name)
    }

    fun updateUser(id: Int, name: String, age: Int): Boolean = transaction {
        Users.update({ Users.id eq id }) {
            it[Users.name] = name
        } > 0
    }

    fun deleteUser(id: Int): Boolean = transaction {
        Users.deleteWhere { Users.id eq id } > 0
    }
}