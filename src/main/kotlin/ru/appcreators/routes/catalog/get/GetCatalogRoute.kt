package ru.appcreators.routes.catalog.get

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.appcreators.repositories.CatalogRepository
import ru.appcreators.repositories.ItemsRepository
import ru.appcreators.routes.catalog.ResponseCatalog

fun Route.getCatalogRoute(
    catalogRepository: CatalogRepository,
    itemsRepository: ItemsRepository,
) {
    get {
        val own = call.request.queryParameters["own"]?.toBooleanStrictOrNull()
            ?: true

        // jwt
        val principal = call.principal<JWTPrincipal>()
        val userId = principal!!.payload.getClaim("userId").asInt()!!

        val catalog = catalogRepository.getCatalog(
            userId = userId,
            own = own
        )

        call.respond(catalog.map {
            ResponseCatalog(
                catalog = it,
                userId = userId,
                items = itemsRepository.getItems(it.id)
            )
        })
    }
}