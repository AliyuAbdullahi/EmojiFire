package com.fire.routes

import com.fire.api.JwtService
import com.fire.global.Constants
import com.fire.models.LoginRequest
import com.fire.models.SignUp
import com.fire.models.SignUpRequest
import com.fire.models.UserResponse
import com.fire.repository.IRepository
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route

const val SIGNOUT = "/signout"

const val LOGIN = "/login"

@KtorExperimentalLocationsAPI
@Location(LOGIN)
class Login

@KtorExperimentalLocationsAPI
@Location(SIGNOUT)
class SignOut


@KtorExperimentalLocationsAPI
fun Route.login(db: IRepository, jwtService: JwtService) {
    post<Login> {
        val loginRequest = call.receive<LoginRequest>()
        val user = db.getUserByEmailAndPassword(loginRequest.email, loginRequest.password)
        if (user == null) {
            application.log.error("User with email ${loginRequest.email} does not exist")
            call.respond(HttpStatusCode.BadRequest, "User with email ${loginRequest.email} does not exist on database")
        } else {
            val token = jwtService.generateToken(user)
            val userResponse = UserResponse(user.userId, user.email, user.displayName, token)
            call.respond(userResponse)
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.signUp(db: IRepository) {
    post<SignUp> {
        val signUpRequest = call.receive<SignUpRequest>()
        when {
            db.getUserByEmail(signUpRequest.email) != null -> call.respond(
                HttpStatusCode.BadRequest,
                "Error, user already registered"
            )
            signUpRequest.password.trim().length < Constants.PASSWORD_LENGTH -> call.respond(
                HttpStatusCode.BadRequest,
                "Error, password must be at least ${Constants.PASSWORD_LENGTH} characters in length"
            )
            else -> {
                try {
                    db.createUser(signUpRequest.email, signUpRequest.displayName, signUpRequest.password)
                    call.respondText("User created successfully", ContentType.Text.Plain)
                } catch (error: Throwable) {
                    application.log.error("Error: ${error.localizedMessage}")
                    call.respond(HttpStatusCode.ExpectationFailed, "Unable to register user")
                }
            }
        }
    }
}