package ru.appcreators.routes.user.login

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.appcreators.plugins.UserException
import ru.appcreators.repositories.RefreshTokensRepository
import ru.appcreators.repositories.UsersRepository
import ru.appcreators.routes.user.generateTokenPair
import ru.appcreators.routes.user.ip
import ru.appcreators.utils.sha256

fun Route.loginUserRoute(
    usersRepository: UsersRepository,
    refreshTokensRepository: RefreshTokensRepository,
) {
    post("/login") {
        val request = call.receive<LoginUserRequest>()

        val dbUser = usersRepository.getUserByName(request.username) ?: throw UserException.NotFound(request.username)
        if (dbUser.password != request.password.sha256()) throw UserException.WrongPassword()
        if (!dbUser.confirmed) throw UserException.UserNotConfirmed()

        val tokenPair = generateTokenPair(dbUser.id, dbUser.name)

        // insert refresh token
        refreshTokensRepository.insertToken(
            userId = dbUser.id,
            refreshToken = tokenPair.refreshToken,
            expiresAt = tokenPair.refreshTokenExpiresAt
        )

        // update ip
        usersRepository.updateIp(dbUser.id, call.ip)

        call.respond(LoginUserResponse(tokenPair.accessToken, tokenPair.refreshToken, tokenPair.refreshTokenExpiresAt))
    }
}