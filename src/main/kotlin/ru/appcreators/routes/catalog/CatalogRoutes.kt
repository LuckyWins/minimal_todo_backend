package ru.appcreators.routes.catalog

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import ru.appcreators.repositories.*
import ru.appcreators.routes.catalog.add.addCatalogRoute
import ru.appcreators.routes.catalog.add_secret.addCatalogSecretRoute
import ru.appcreators.routes.catalog.get.getCatalogRoute
import ru.appcreators.routes.catalog.share.shareCatalogRoute

@Serializable
data class ResponseCatalog(
    val id: Int,
    val own: Boolean,
    val ownerId: Int,
    val title: String,
    val items: List<ResponseCatalogItem>,
) {
    constructor(
        catalog: Catalog,
        userId: Int,
        items: List<Item>,
    ) : this (
        id = catalog.id,
        own = catalog.ownerId == userId,
        ownerId = catalog.ownerId,
        title = catalog.title,
        items = items.map {ResponseCatalogItem(
            id = it.id,
            done = it.done,
            value = it.value
        )}
    )
}

@Serializable
data class ResponseCatalogItem(
    val id: Int,
    val done: Boolean,
    val value: String?,
)

fun Application.catalogRoutes(
    catalogRepository: CatalogRepository,
    catalogShareSecretRepository: CatalogShareSecretRepository,
    catalogShareRepository: CatalogShareRepository,
    itemsRepository: ItemsRepository,
) {
    routing {
        authenticate("auth-jwt") {
            route("/catalog") {
                getCatalogRoute(catalogRepository, itemsRepository)
                addCatalogRoute(catalogRepository)
                route("/{id}") {
                    shareCatalogRoute(
                        catalogRepository = catalogRepository,
                        catalogShareSecretRepository = catalogShareSecretRepository
                    )
                }
                addCatalogSecretRoute(
                    catalogShareSecretRepository = catalogShareSecretRepository,
                    catalogShareRepository = catalogShareRepository,
                    catalogRepository = catalogRepository,
                    itemsRepository = itemsRepository
                )
            }
        }
    }
}