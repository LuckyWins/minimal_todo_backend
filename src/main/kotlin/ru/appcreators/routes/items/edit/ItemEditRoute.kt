package ru.appcreators.routes.items.edit

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.appcreators.plugins.ItemException
import ru.appcreators.repositories.ItemsRepository

fun Route.editItemRoute(
    itemsRepository: ItemsRepository,
) {
    post("/edit") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: throw MissingRequestParameterException("id")

        val request = call.receive<ItemEditRequest>()
        if (request.done == null && request.value == null)
            throw ItemException.NothingToUpdate(id)

        // jwt
        val principal = call.principal<JWTPrincipal>()
        val userId = principal!!.payload.getClaim("userId").asInt()!!

        val item = itemsRepository.getItem(id)
            ?: throw ItemException.NotFound(id)

        // check is catalog ownerId == you
        if (item.ownerId != userId)
            throw ItemException.NotOwner(id)

        val updateResult = itemsRepository.updateItem(
            id = id,
            done = request.done,
            value = request.value
        )
        if (!updateResult) throw ItemException.UnableToUpdate(id)

        call.respond(HttpStatusCode.OK)
    }
}