package ru.appcreators.routes.user.refresh

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.appcreators.plugins.AuthorizationException
import ru.appcreators.repositories.RefreshToken
import ru.appcreators.repositories.RefreshTokensRepository
import ru.appcreators.repositories.UsersRepository
import ru.appcreators.routes.user.generateTokenPair

fun Route.refreshUserRoute(
    usersRepository: UsersRepository,
    refreshTokensRepository: RefreshTokensRepository,
) {
    get("/refresh/{refreshToken}") {
        val refreshToken = call.parameters["refreshToken"]
            ?: throw MissingRequestParameterException("refreshToken")

        val dbRefreshToken: RefreshToken = refreshTokensRepository.getToken(refreshToken)
            ?: throw AuthorizationException()

        val currentTime = System.currentTimeMillis()

        if (dbRefreshToken.expiresAt < currentTime) throw AuthorizationException()

        val dbUser = usersRepository.getUserById(dbRefreshToken.userId) ?: throw AuthorizationException()

        val tokenPair = generateTokenPair(dbUser.id, dbUser.name)

        refreshTokensRepository.updateToken(refreshToken, tokenPair.refreshToken, tokenPair.refreshTokenExpiresAt)

        call.respond(RefreshUserResponse(tokenPair.accessToken, tokenPair.refreshToken, tokenPair.refreshTokenExpiresAt))
    }
}