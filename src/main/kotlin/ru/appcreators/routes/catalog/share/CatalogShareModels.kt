package ru.appcreators.routes.catalog.share

import kotlinx.serialization.Serializable

@Serializable
data class ShareCatalogResponse(
    val shareSecret: String,
)