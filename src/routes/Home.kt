package com.fire.routes

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get

const val HOME = "/"

fun Route.home() {
    get("/") {
        call.respondText(contentType = ContentType.Text.Plain, text = "Hello Ktor")
    }
}