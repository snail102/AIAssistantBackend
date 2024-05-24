package com.example

import com.example.plugins.*
import com.example.utils.loadProperties
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.ProxyBuilder.http
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun buildCurlCommand(request: HttpRequestBuilder): String {
    return buildString {
        append("curl -X ${request.method.value} \\\n")
        request.headers.entries().forEach { (key, values) ->
            values.forEach { value ->
                append("  -H \"$key: $value\" \\\n")
            }
        }
        val body = request.body
        if (body !is EmptyContent) {
            append("  -d '${body}' \\\n")
        }
        append(request.url.toString())
    }
}


val client = HttpClient(CIO) {
    val properties = loadProperties()
    val apiKey = properties.getProperty("API_KEY")
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
    engine {
        proxy = ProxyBuilder.http("http://147.45.74.57:3128/")
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
    }
    defaultRequest {
        contentType(ContentType.Application.Json)
        bearerAuth(apiKey)
    }
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureRouting()
}
