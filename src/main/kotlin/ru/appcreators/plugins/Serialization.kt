package ru.appcreators.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            // remove if you want to ignore unknown json fields
            ignoreUnknownKeys = true
            // remove nulls & etc. default values
            explicitNulls = false
        })
    }
}
