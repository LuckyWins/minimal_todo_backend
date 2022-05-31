package ru.appcreators.routes.items

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import ru.appcreators.repositories.CatalogRepository
import ru.appcreators.repositories.ItemsRepository
import ru.appcreators.routes.items.add.addItemRoute
import ru.appcreators.routes.items.delete.deleteItemRoute
import ru.appcreators.routes.items.edit.editItemRoute

fun Application.itemsRoutes(
    itemsRepository: ItemsRepository,
    catalogRepository: CatalogRepository,
) {
    routing {
        authenticate("auth-jwt") {
            route("/items") {
                addItemRoute(itemsRepository, catalogRepository)
                route("/{id}") {
                    deleteItemRoute(itemsRepository)
                    editItemRoute(itemsRepository)
                }
            }
        }
    }
}