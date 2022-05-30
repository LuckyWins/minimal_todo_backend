package ru.appcreators.routes.user.refresh

import kotlinx.serialization.Serializable

@Serializable
class RefreshUserResponse(
    val accessToken: String,
    val refreshToken: String,
    val refreshTokenExpiresAt: Long
) // : Response()