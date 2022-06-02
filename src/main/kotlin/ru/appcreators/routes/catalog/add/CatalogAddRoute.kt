package ru.appcreators.routes.catalog.add

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.appcreators.repositories.CatalogRepository
import ru.appcreators.repositories.ItemsRepository
import ru.appcreators.routes.catalog.ResponseCatalog

fun Route.addCatalogRoute(
    catalogRepository: CatalogRepository,
    itemsRepository: ItemsRepository,
) {
    post("/add") {
        val request = call.receive<AddCatalogRequest>()

        // jwt
        val principal = call.principal<JWTPrincipal>()
        val userId = principal!!.payload.getClaim("userId").asInt()!!

        // add catalog
        val addedCatalogId = catalogRepository.addCatalog(
            userId = userId,
            title = request.title
        )

        val addedCatalog = catalogRepository.getCatalogSingle(addedCatalogId)!!

        call.respond(ResponseCatalog(
            catalog = addedCatalog,
            userId = userId,
            items = itemsRepository.getItems(addedCatalogId)
        ))
    }
}