package com.example.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime
import java.time.ZoneOffset
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

    private fun generateAccessToken(userId: Int, dateExpiresAt: Date): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withAudience(audience)
        .withClaim("userId", userId)
        .withExpiresAt(dateExpiresAt)
        .sign(algorithm)

    private fun generateRefreshToken(userId: Int, dateExpiresAt: Date): String = JWT.create()
        .withSubject("Refresh")
        .withIssuer(issuer)
        .withAudience(audience)
        .withClaim("userId", userId)
        .withExpiresAt(dateExpiresAt)
        .sign(algorithm)

    fun getTokens(userId: Int): GeneratedTokens {
        val localDateTime = LocalDateTime.now()

        val accessDateTime = localDateTime.plusMinutes(15)
        val refreshDateTime = localDateTime.plusDays(7)

        val accessExpiresAt = Date(accessDateTime.toInstant(ZoneOffset.UTC).toEpochMilli())
        val refreshExpiresAt = Date(refreshDateTime.toInstant(ZoneOffset.UTC).toEpochMilli())

        val access = generateAccessToken(userId = userId, dateExpiresAt = accessExpiresAt)
        val refresh = generateRefreshToken(userId = userId, dateExpiresAt = refreshExpiresAt)

        return GeneratedTokens(
            userId = userId,
            access = access,
            accessExpiresAt = accessDateTime.toKotlinLocalDateTime(),
            refresh = refresh,
            refreshExpiresAt = refreshDateTime.toKotlinLocalDateTime()
        )
    }

    fun verifyToken(token: String): DecodedJWT = verifier.verify(token)
}