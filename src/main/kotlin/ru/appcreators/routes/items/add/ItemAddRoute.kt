package ru.appcreators.routes.items.add

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.appcreators.plugins.CatalogException
import ru.appcreators.repositories.CatalogRepository
import ru.appcreators.repositories.ItemsRepository

fun Route.addItemRoute(
    itemsRepository: ItemsRepository,
    catalogRepository: CatalogRepository,
) {
    post("/add") {
        val request = call.receive<ItemAddRequest>()

        // jwt
        val principal = call.principal<JWTPrincipal>()
        val userId = principal!!.payload.getClaim("userId").asInt()!!

        val catalog = catalogRepository.getCatalogSingle(request.catalogId)
            ?: throw CatalogException.NotFound(request.catalogId)

        // check is catalog ownerId == you
        if (catalog.ownerId != userId)
            throw CatalogException.NotOwner(request.catalogId)

        val addedItemId = itemsRepository.addItem(
            catalogId = request.catalogId,
            done = request.done,
            value = request.value
        )

        call.respond(
            ItemAddResponse(
            id = addedItemId
        )
        )
    }
}