package ru.appcreators.routes.catalog.add_secret

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.appcreators.plugins.CatalogException
import ru.appcreators.repositories.CatalogRepository
import ru.appcreators.repositories.CatalogShareRepository
import ru.appcreators.repositories.CatalogShareSecretRepository
import ru.appcreators.repositories.ItemsRepository
import ru.appcreators.routes.catalog.ResponseCatalog

fun Route.addCatalogSecretRoute(
    catalogShareSecretRepository: CatalogShareSecretRepository,
    catalogShareRepository: CatalogShareRepository,
    catalogRepository: CatalogRepository,
    itemsRepository: ItemsRepository,
) {
    get("/addSecret/{shareSecret}") {
        val shareSecret = call.parameters["shareSecret"]
            ?: throw MissingRequestParameterException("shareSecret")

        // jwt
        val principal = call.principal<JWTPrincipal>()
        val userId = principal!!.payload.getClaim("userId").asInt()!!

        // check is exists in invites
        val shareSecretItem = catalogShareSecretRepository.getSecretByValue(shareSecret)
            ?: throw CatalogException.SecretShareNotFound(shareSecret)

        // check is already granted
        val isAlreadyGranted = catalogShareRepository.isShareExists(
            userId = userId,
            catalogId = shareSecretItem.catalogId
        )
        if (isAlreadyGranted) throw CatalogException.ShareAlreadyGranted(
            shareSecret = shareSecret,
            userId = userId
        )

        // add new share
        catalogShareRepository.addShare(
            userId = userId,
            catalogId = shareSecretItem.catalogId
        )

        val catalogSingle = catalogRepository.getCatalogSingle(shareSecretItem.catalogId)!!

        call.respond(ResponseCatalog(
            catalog = catalogSingle,
            userId = userId,
            items = itemsRepository.getItems(catalogSingle.id)
        ))

    }
}