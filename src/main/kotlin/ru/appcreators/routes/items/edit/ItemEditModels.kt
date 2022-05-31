package ru.appcreators.routes.items.edit

import kotlinx.serialization.Serializable

@Serializable
data class ItemEditRequest(
    val done: Boolean? = null,
    val value: String? = null,
)