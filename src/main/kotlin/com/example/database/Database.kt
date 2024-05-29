import com.example.database.DatabaseFactory

import org.koin.dsl.module

val databaseModule = module {

    single {
        DatabaseFactory.init()
    }
}