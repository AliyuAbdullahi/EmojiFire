package com.fire.api

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.fire.models.User
import java.util.*

class JwtService {
    private val issuer = "emojiphrases"
    private val jwtSecret = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    private fun expireAt() = Date(System.currentTimeMillis() + 3_600_000 * 24 ) // 24 hours

    fun generateToken(user: User): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", user.userId)
        .withExpiresAt(expireAt())
        .sign(algorithm)

}