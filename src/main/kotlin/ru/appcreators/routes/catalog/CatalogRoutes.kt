package ru.appcreators.routes.catalog

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import ru.appcreators.repositories.Catalog
import ru.appcreators.repositories.CatalogRepository
import ru.appcreators.repositories.CatalogShareSecretRepository
import ru.appcreators.routes.catalog.add.addCatalogRoute
import ru.appcreators.routes.catalog.get.getCatalogRoute
import ru.appcreators.routes.catalog.share.shareCatalogRoute

@Serializable
data class ResponseCatalog(
    val id: Int,
    val own: Boolean,
    val ownerId: Int,
    val title: String
) {
    constructor(
        catalog: Catalog,
        userId: Int,
    ) : this (
        id = catalog.id,
        own = catalog.ownerId == userId,
        ownerId = catalog.ownerId,
        title = catalog.title
    )
}

fun Application.catalogRoutes(
    catalogRepository: CatalogRepository,
    catalogShareSecretRepository: CatalogShareSecretRepository
) {
    routing {
        authenticate("auth-jwt") {
            route("/catalog") {
                getCatalogRoute(catalogRepository)
                addCatalogRoute(catalogRepository)
                route("/{id}") {
                    shareCatalogRoute(
                        catalogRepository = catalogRepository,
                        catalogShareSecretRepository = catalogShareSecretRepository
                    )
                }
            }
        }
    }
}