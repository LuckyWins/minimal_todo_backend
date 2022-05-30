package ru.appcreators

import io.ktor.server.application.*
import ru.appcreators.data.local.AppConfig
import ru.appcreators.data.remote.DatabaseFactory
import ru.appcreators.plugins.*
import ru.appcreators.repositories.*
import ru.appcreators.routes.catalog.catalogRoutes
import ru.appcreators.routes.user.userRoutes

fun main(args: Array<String>): Unit =
    io.ktor.server.tomcat.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    // INIT

//    val dataSource = DatabaseFactory.hikari()
    DatabaseFactory.connect()

    // REPOS

    val usersRepository = UsersRepository()
    val refreshTokensRepository = RefreshTokensRepository()
    val registrationConfirmRepository = RegistrationConfirmRepository()
    val catalogRepository = CatalogRepository()
    val catalogShareSecretRepository = CatalogShareSecretRepository()
    val catalogShareRepository = CatalogShareRepository()

    val mailService = MailService(
        username = AppConfig.mailAuthUsername,
        password = AppConfig.mailAuthPassword
    )

    // PLUGINS

    // exceptions
    configureExceptions()
    // auth
    configureAuth()
    // logs
    configureLogging()
    // serialization
    configureSerialization()
    // CORS
    configureCORS()

    // START/STOP

    val stopping: (Application) -> Unit = {

    }
    var stopped: (Application) -> Unit = {}

    stopped = {
        this.environment.monitor.unsubscribe(ApplicationStopping, stopping)
        this.environment.monitor.unsubscribe(ApplicationStopped, stopped)
    }

    this.environment.monitor.subscribe(ApplicationStopping, stopping)
    this.environment.monitor.subscribe(ApplicationStopped, stopped)

    // ROUTES
    userRoutes(
        usersRepository = usersRepository,
        refreshTokensRepository = refreshTokensRepository,
        registrationConfirmRepository = registrationConfirmRepository,
        mailService = mailService
    )
    catalogRoutes(
        catalogRepository = catalogRepository,
        catalogShareSecretRepository = catalogShareSecretRepository,
        catalogShareRepository = catalogShareRepository
    )
}
