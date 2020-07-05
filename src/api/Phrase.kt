package com.fire.api

import com.fire.API_VERSION
import com.fire.models.EmojiPhrase
import com.fire.redirect
import com.fire.repository.IRepository
import com.fire.requests.Request
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.locations.get
import io.ktor.locations.post

const val PHRASE_ENDPOINT = "$API_VERSION/phrase"

const val PHRASES = "/phrases"

@KtorExperimentalLocationsAPI
@Location(PHRASE_ENDPOINT)
class PostPhrase

@KtorExperimentalLocationsAPI
@Location(PHRASES)
class GetPhrases

@KtorExperimentalLocationsAPI
fun Route.phrase(db: IRepository) {
    authenticate("jwt") {
        post<PostPhrase> {
            val request = call.receive<Request>()
            val phrase = db.add(request.emoji, request.phrase, request.userId)
            call.redirect(GetPhrases())
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.phrases(db: IRepository) {
    authenticate("jwt") {
        get<GetPhrases> {
            val phrases = db.getPhrases()
            call.respond(phrases)
        }
    }
}