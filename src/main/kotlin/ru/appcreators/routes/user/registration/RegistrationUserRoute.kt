package ru.appcreators.routes.user.registration

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.appcreators.data.local.AppConfig
import ru.appcreators.plugins.UserException
import ru.appcreators.repositories.MailService
import ru.appcreators.repositories.RegistrationConfirmRepository
import ru.appcreators.repositories.UsersRepository
import ru.appcreators.routes.user.ip
import ru.appcreators.routes.user.withOffset
import ru.appcreators.utils.sha256
import java.util.*

fun Route.registrationUserRoute(
    usersRepository: UsersRepository,
    registrationConfirmRepository: RegistrationConfirmRepository,
    mailService: MailService,
) {
    post() {
        val request = call.receive<RegistrationUserRequest>()

        // check username availability
        val isUsernameExists = usersRepository.getUserByName(request.username) != null
        if (isUsernameExists) throw UserException.UsernameAlreadyExists()

        // check email availability
        val isEmailExists = usersRepository.getUserByEmail(request.email) != null
        if (isEmailExists) throw UserException.EmailAlreadyExists()

        // add new user
        usersRepository.createUser(request.username, call.ip, request.email, request.password.sha256())
        // get user info
        val dbUser = usersRepository.getUserByName(request.username)!!

        val confirmToken = UUID.randomUUID().toString()
        val currentTime = System.currentTimeMillis()

        // save confirmToken
        registrationConfirmRepository.saveToken(
            userId = dbUser.id,
            confirmToken = confirmToken,
            expiresAt = currentTime.withOffset(AppConfig.registrationConfirmTokenLifetime)
        )

        // send email with confirmation link
        // uncomment if you want to wait sending
//        runBlocking {
        val job = launch(Dispatchers.Default) {
            mailService.sendMail(
                to = request.email,
                subject = "Registration",
                text = "Hi! to finish your registration follow the link ${AppConfig.registrationConfirmUrl}$confirmToken"
            )
        }
//        }

        call.respond(HttpStatusCode.OK)
    }
}