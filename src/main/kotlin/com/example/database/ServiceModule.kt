package com.example.database

import org.koin.dsl.module

val serviceModule = module {
    single { UserService() }
}