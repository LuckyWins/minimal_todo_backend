package ru.appcreators.routes.catalog.add

import kotlinx.serialization.Serializable

@Serializable
data class AddCatalogRequest(
    val title: String,
)