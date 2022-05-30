package ru.appcreators.routes.user.registration

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationUserRequest(
    val username: String,
    val email: String,
    val password: String
)