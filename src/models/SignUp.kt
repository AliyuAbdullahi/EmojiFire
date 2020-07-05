package com.fire.models

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

const val SIGNUP = "/signup"


@KtorExperimentalLocationsAPI
@Location(SIGNUP)
data class SignUp(val userId: String = "", val displayName: String = "", val email: String = "", val error: String = "")