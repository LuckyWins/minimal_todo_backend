package ru.appcreators.routes.items.add

import kotlinx.serialization.Serializable

@Serializable
data class ItemAddRequest(
    val catalogId: Int,
    val done: Boolean? = null,
    val value: String? = null,
)