package ru.appcreators.routes.user.registration_confirm

import kotlinx.serialization.Serializable

@Serializable
class RegistrationConfirmUserResponse(
    val accessToken: String,
    val refreshToken: String,
    val refreshTokenExpiresAt: Long
) // : Response()