package ru.appcreators.data.models

import kotlinx.serialization.Serializable

@Serializable
open class Response {
    //    @SerialName("ErrorCode")
    var error: String? = null
}