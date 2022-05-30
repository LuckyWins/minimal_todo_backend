package ru.appcreators.data.local

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import java.time.Duration

object AppConfig {
    private val mainConfig = HoconApplicationConfig(ConfigFactory.load("application.conf"))
    private val config = HoconApplicationConfig(ConfigFactory.load("base.conf"))

    // jwt
    val jwtSecret: String
        get() = mainConfig.property("jwt.access.secret").getString()
    val jwtIssuer: String
        get() = mainConfig.property("jwt.issuer").getString()
    val jwtAudience: String
        get() = mainConfig.property("jwt.audience").getString()
    val jwtRealm: String
        get() = mainConfig.property("jwt.realm").getString()
    /** In minutes */
    val jwtAccessTokenLifetime: Duration
        get() = Duration.ofMinutes(mainConfig.property("jwt.access.lifetime").getString().toLong())
    /** In days */
    val jwtRefreshTokenLifetime: Duration
        get() = Duration.ofDays(mainConfig.property("jwt.refresh.lifetime").getString().toLong())
    // registration
    /** In days */
    val registrationConfirmTokenLifetime: Duration
        get() = Duration.ofDays(config.property("todo.registration.confirm.lifetime").getString().toLong())
    val registrationConfirmUrl: String
        get() = config.property("todo.registration.confirm.url").getString()
    // mail auth
    val mailAuthUsername: String
        get() = config.property("todo.mail.auth.username").getString()
    val mailAuthPassword: String
        get() = config.property("todo.mail.auth.password").getString()
    // smtp
    val mailSmtpHost: String
        get() = config.property("todo.mail.smtp.host").getString()
    val mailSmtpPort: String
        get() = config.property("todo.mail.smtp.port").getString()
    val mailSmtpStarttls: String
        get() = config.property("todo.mail.smtp.starttls").getString()
    //
}