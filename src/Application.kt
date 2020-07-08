package com.fire

import com.fire.api.JwtService
import com.fire.api.phrase
import com.fire.api.phrases
import com.fire.repository.DatabaseFactory
import com.fire.repository.EmojiPhraseRepository
import com.fire.routes.about
import com.fire.routes.home
import com.fire.routes.login
import com.fire.routes.signUp
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.locations.locations
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.routing

const val API_VERSION = "/api/v1"

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(DefaultHeaders)
    install(Locations)
    install(StatusPages) {
        exception<Throwable> { error ->
            call.respondText(error.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    DatabaseFactory.init()

    val db = EmojiPhraseRepository()

    val jwtService = JwtService()


    // ------- JSON Web Token Auth -------- //
    install(Authentication) {
        jwt(name = "jwt") {
            verifier(jwtService.verifier)
            realm = "emoji phrase app"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asString()
                val user = db.getUserById(claimString)
                user
            }
        }
    }


    // -------- Routes ------ //
    routing {
        home()
        about()
        phrase(db)
        phrases(db)
        signUp(db)
        login(db, jwtService)
    }
}

@KtorExperimentalLocationsAPI
suspend fun ApplicationCall.redirect(location: Any) {
    respondRedirect(application.locations.href(location))
}

