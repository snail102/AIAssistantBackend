package com.example.mailSender

import org.koin.dsl.module

val mailSenderModule = module {
    single<MailSender> { MailSenderMailRu() }
}