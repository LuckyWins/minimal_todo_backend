package ru.appcreators.routes.user.registration_confirm

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.appcreators.plugins.RegistrationTokenException
import ru.appcreators.repositories.RefreshTokensRepository
import ru.appcreators.repositories.RegistrationConfirmRepository
import ru.appcreators.repositories.UsersRepository
import ru.appcreators.routes.user.generateTokenPair
import ru.appcreators.routes.user.ip

fun Route.registrationConfirmUserRoute(
    usersRepository: UsersRepository,
    refreshTokensRepository: RefreshTokensRepository,
    registrationConfirmRepository: RegistrationConfirmRepository,
) {
    get("/confirm/{confirmToken}") {
        val confirmToken = call.parameters["confirmToken"]
            ?: throw MissingRequestParameterException("confirmToken")

        // check confirmToken exists
        val dbConfirmToken = registrationConfirmRepository.getToken(confirmToken)
            ?: throw RegistrationTokenException.NotFound(confirmToken)

        // confirm user
        usersRepository.confirmUser(dbConfirmToken.userId)

        // delete confirmToken
        registrationConfirmRepository.deleteToken(confirmToken)

        // auth user
        val dbUser = usersRepository.getUserById(dbConfirmToken.userId)!!

        val tokenPair = generateTokenPair(dbUser.id, dbUser.name)

        // insert refresh token
        refreshTokensRepository.insertToken(
            userId = dbUser.id,
            refreshToken = tokenPair.refreshToken,
            expiresAt = tokenPair.refreshTokenExpiresAt
        )

        // update ip
        usersRepository.updateIp(dbUser.id, call.ip)

        call.respond(RegistrationConfirmUserResponse(tokenPair.accessToken, tokenPair.refreshToken, tokenPair.refreshTokenExpiresAt))
    }
}