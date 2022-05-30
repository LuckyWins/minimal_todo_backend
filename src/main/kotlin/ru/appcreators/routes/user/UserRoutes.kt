package ru.appcreators.routes.user

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import ru.appcreators.data.local.AppConfig
import ru.appcreators.repositories.MailService
import ru.appcreators.repositories.RefreshTokensRepository
import ru.appcreators.repositories.RegistrationConfirmRepository
import ru.appcreators.repositories.UsersRepository
import ru.appcreators.routes.user.login.loginUserRoute
import ru.appcreators.routes.user.refresh.refreshUserRoute
import ru.appcreators.routes.user.registration.registrationUserRoute
import ru.appcreators.routes.user.registration_confirm.registrationConfirmUserRoute
import java.time.Duration
import java.util.*

data class TokenPair(val accessToken: String, val refreshToken: String, val refreshTokenExpiresAt: Long)

fun Long.withOffset(offset: Duration) = this + offset.toMillis()

val ApplicationCall.ip: String
    get() = this.request.header("x-forwarded-for") ?: this.request.origin.remoteHost

fun generateTokenPair(userId: Int, username: String): TokenPair {
    val currentTime = System.currentTimeMillis()

    val accessToken = JWT.create()
//        .withSubject(userId.toString())
        .withAudience(AppConfig.jwtAudience)
        .withIssuer(AppConfig.jwtIssuer)
        .withClaim("userId", userId)
//        .withClaim("username", username)
        .withExpiresAt(Date(currentTime.withOffset(AppConfig.jwtAccessTokenLifetime)))
        .sign(Algorithm.HMAC256(AppConfig.jwtSecret))

    val refreshToken = UUID.randomUUID().toString()

    return TokenPair(
        accessToken = accessToken,
        refreshToken = refreshToken,
        refreshTokenExpiresAt = currentTime.withOffset(AppConfig.jwtRefreshTokenLifetime)
    )
}

fun Application.userRoutes(
    usersRepository: UsersRepository,
    refreshTokensRepository: RefreshTokensRepository,
    registrationConfirmRepository: RegistrationConfirmRepository,
    mailService: MailService
) {
    routing {
        route("/user") {
            loginUserRoute(usersRepository, refreshTokensRepository)
            refreshUserRoute(usersRepository, refreshTokensRepository)
            route("/registration") {
                registrationUserRoute(usersRepository, registrationConfirmRepository, mailService)
                registrationConfirmUserRoute(usersRepository, refreshTokensRepository, registrationConfirmRepository)
            }
        }
    }
}