package ru.appcreators.repositories

import ru.appcreators.data.local.AppConfig
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class MailService(
    private val username: String,
    private val password: String
) {

    private val properties by lazy { Properties().apply {
        this["mail.smtp.host"] = AppConfig.mailSmtpHost
        this["mail.smtp.port"] = AppConfig.mailSmtpPort
        this["mail.smtp.auth"] = "true"
        this["mail.smtp.starttls.enable"] = AppConfig.mailSmtpStarttls // TLS
    }}

    private val session: Session
        get() {
            return Session.getInstance(properties,
                object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(username, password)
                    }
                })
        }

    @Throws(MessagingException::class)
    fun sendMail(to: String, subject: String, text: String) {
        val message = MimeMessage(session).apply {
            this.setFrom(InternetAddress(username))
            this.setRecipients(Message.RecipientType.TO, to)
            this.subject = subject
            this.setText(text)
//            this.setContent("<p>$text</p>", "text/html")
        }

        Transport.send(message)
    }
}