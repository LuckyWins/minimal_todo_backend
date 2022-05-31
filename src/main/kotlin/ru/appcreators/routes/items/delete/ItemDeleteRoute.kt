package ru.appcreators.routes.items.delete

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.appcreators.plugins.ItemException
import ru.appcreators.repositories.ItemsRepository

fun Route.deleteItemRoute(
    itemsRepository: ItemsRepository,
) {
    delete {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: throw MissingRequestParameterException("id")

        // jwt
        val principal = call.principal<JWTPrincipal>()
        val userId = principal!!.payload.getClaim("userId").asInt()!!

        val item = itemsRepository.getItem(id)
            ?: throw ItemException.NotFound(id)

        // check is catalog ownerId == you
        if (item.ownerId != userId)
            throw ItemException.NotOwner(id)

        val deleteResult = itemsRepository.deleteItem(id)
        if (!deleteResult) throw ItemException.UnableToDelete(id)

        call.respond(HttpStatusCode.OK)
    }
}