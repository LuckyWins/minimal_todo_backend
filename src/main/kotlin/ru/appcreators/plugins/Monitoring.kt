package ru.appcreators.plugins

import io.ktor.server.plugins.callloging.*
import org.slf4j.event.*
import io.ktor.server.request.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.configureLogging() {

    intercept(ApplicationCallPipeline.Call) {
        proceed()
    }

    install(CallLogging) {
//        level = Level.INFO
        level = Level.TRACE
//        filter { call -> call.request.path().startsWith("/") }
    }

}
