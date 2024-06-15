package com.example.database

import com.example.utils.loadProperties
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

private fun provideDataSource(url:String,driverClass:String): HikariDataSource {
    val hikariConfig= HikariConfig().apply {
        driverClassName=driverClass
        jdbcUrl=url
        maximumPoolSize=30
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    return HikariDataSource(hikariConfig)
}

object  DatabaseFactory {

    fun init() {
        val properties = loadProperties()
        val jdbcUrl=properties.getProperty("jdbcURL")
        Database.connect(provideDataSource(jdbcUrl,"org.postgresql.Driver"))

        transaction {
            SchemaUtils.create(Users)
            SchemaUtils.create(Tokens)
            SchemaUtils.create(Messages)
            SchemaUtils.create(Chats)
        }
    }
}