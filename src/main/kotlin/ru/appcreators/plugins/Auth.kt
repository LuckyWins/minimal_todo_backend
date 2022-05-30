package ru.appcreators.plugins

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import ru.appcreators.data.local.AppConfig

fun Application.configureAuth() {

    authentication {
        jwt("auth-jwt") {
            val jwtAudience = AppConfig.jwtAudience
            realm = AppConfig.jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(AppConfig.jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(AppConfig.jwtIssuer)
                    .build()
            )
            validate { credential ->
//                    if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
                // TODO {check expiresAt}
//                    val timestamp = System.currentTimeMillis() * 1000
//                    val exp = credential.payload.getClaim("exp").asInt()
//                    if (exp)
                if (credential.payload.getClaim("userId").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

}