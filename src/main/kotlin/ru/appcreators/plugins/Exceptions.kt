package ru.appcreators.plugins

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.configureExceptions() {
    install(StatusPages) {
        exception<AuthenticationException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, hashMapOf("error" to cause.message))
        }
        exception<AuthorizationException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, hashMapOf("error" to cause.message))
        }
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, hashMapOf("error" to cause.message))
        }
    }
}

// all exceptions list
class AuthenticationException: Throwable("Unauthorized")
class AuthorizationException: Throwable("Forbidden")

object UserException {
    class NotFound(name: String): Throwable("User ($name) not found")
    class WrongPassword: Throwable("Wrong password")
    class UserNotConfirmed: Throwable("User not confirmed")
    class UsernameAlreadyExists: Throwable("Username already exists")
    class EmailAlreadyExists: Throwable("Email already exists")
}

object RegistrationTokenException {
    class NotFound(token: String): Throwable("Token ($token) not found")
}

object CatalogException {
    class NotFound(id: Int): Throwable("Catalog ($id) not found")
    class NotOwner(id: Int): Throwable("Catalog ($id) dos not belong to you")

    class SecretShareNotFound(value: String): Throwable("Catalog shareSecret ($value) not found")

    class ShareAlreadyGranted(shareSecret: String, userId: Int): Throwable("Catalog shareSecret ($shareSecret) already granted for userId ($userId)")
}

object ItemException {
    class NotFound(id: Int): Throwable("Item ($id) not found")
    class NotOwner(id: Int): Throwable("Item ($id) dos not belong to you")
    class UnableToDelete(id: Int): Throwable("Item ($id): unable to delete")
    class NothingToUpdate(id: Int): Throwable("Item ($id): nothing to update, all request fields are empty")
    class UnableToUpdate(id: Int): Throwable("Item ($id): unable to update")
}