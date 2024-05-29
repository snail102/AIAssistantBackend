package com.example.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

object JwtConfig {
    private const val secret = "your-secret-key"
    private const val issuer = "your-issuer"
    const val audience = "your-audience"
    const val realm = "your-realm"
    private const val accessTokenValidityInMs = 15 * 60 * 1000 // 15 минут
    private const val refreshTokenValidityInMs = 7 * 24 * 60 * 60 * 1000 // 7 дней

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    fun generateAccessToken(userId: String): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withAudience(audience)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + accessTokenValidityInMs))
        .sign(algorithm)

    fun generateRefreshToken(userId: String): String = JWT.create()
        .withSubject("Refresh")
        .withIssuer(issuer)
        .withAudience(audience)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenValidityInMs))
        .sign(algorithm)

    fun verifyToken(token: String): DecodedJWT = verifier.verify(token)
}