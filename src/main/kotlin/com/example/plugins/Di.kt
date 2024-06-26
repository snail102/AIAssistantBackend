package com.example.plugins

import com.example.database.serviceModule
import com.example.mailSender.mailSenderModule
import databaseModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureDi() {
    install(Koin) {
        modules(databaseModule, serviceModule,mailSenderModule)
    }
}