import com.example.database.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.defaultheaders.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.configureDatabase() {
    val driverClass=environment.config.property("storage.driverClassName").getString()
    val jdbcUrl=environment.config.property("storage.jdbcURL").getString()
    val db=Database.connect(provideDataSource(jdbcUrl,driverClass))

    transaction(db){
        SchemaUtils.create(Users)
    }
}

private fun provideDataSource(url:String,driverClass:String):HikariDataSource{
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