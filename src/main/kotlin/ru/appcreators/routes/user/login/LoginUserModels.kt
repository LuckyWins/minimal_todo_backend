package ru.appcreators.routes.user.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginUserRequest(
    val username: String,
    val password: String
)

@Serializable
class LoginUserResponse(
    val accessToken: String,
    val refreshToken: String,
    val refreshTokenExpiresAt: Long
) // : Response()