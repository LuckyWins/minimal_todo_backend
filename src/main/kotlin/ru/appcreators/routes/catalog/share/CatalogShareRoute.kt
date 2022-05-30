package ru.appcreators.routes.catalog.share

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.appcreators.plugins.CatalogException
import ru.appcreators.repositories.CatalogRepository
import ru.appcreators.repositories.CatalogShareSecretRepository
import java.util.*

fun Route.shareCatalogRoute(
    catalogRepository: CatalogRepository,
    catalogShareSecretRepository: CatalogShareSecretRepository,
) {
    get("/share") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: throw MissingRequestParameterException("id")

        // jwt
        val principal = call.principal<JWTPrincipal>()
        val userId = principal!!.payload.getClaim("userId").asInt()!!

        val catalogSingle = catalogRepository.getCatalogSingle(id)
            ?: throw CatalogException.NotFound(id)

        // check owner
        if (catalogSingle.ownerId != userId) throw CatalogException.NotOwner(id)

        // check secret
        val currentSecret = catalogShareSecretRepository.getSecret(
            catalogId = id
        )

        val shareSecret = if (currentSecret != null) {
            // return current
            currentSecret
        } else {
            // generate new
            val generatedSecret = UUID.randomUUID().toString()
            catalogShareSecretRepository.addShareSecret(
                shareSecret = generatedSecret,
                catalogId = id
            )
            // return new secret
            generatedSecret
        }

        call.respond(ShareCatalogResponse(
            shareSecret = shareSecret
        ))
    }
}