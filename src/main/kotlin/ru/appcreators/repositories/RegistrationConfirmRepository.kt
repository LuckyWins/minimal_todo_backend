package ru.appcreators.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object RegistrationConfirms : Table("registration_confirm") {
    val userId = integer("user_id").references(Users.id)
    val confirmToken = varchar("confirm_token", 300)
    val expiresAt = long("expires_at")
}

data class RegistrationConfirm(
    val userId: Int,
    val confirmToken: String,
    val expiresAt: Long
) {
    constructor(resultRow: ResultRow) : this(
        userId = resultRow[RegistrationConfirms.userId],
        confirmToken = resultRow[RegistrationConfirms.confirmToken],
        expiresAt = resultRow[RegistrationConfirms.expiresAt]
    )
}

class RegistrationConfirmRepository {

    fun saveToken(userId: Int, confirmToken: String, expiresAt: Long): Boolean = transaction {
        RegistrationConfirms.insert {
            it[RegistrationConfirms.userId] = userId
            it[RegistrationConfirms.confirmToken] = confirmToken
            it[RegistrationConfirms.expiresAt] = expiresAt
        }.insertedCount == 1
    }

    fun getToken(confirmToken: String): RegistrationConfirm? = transaction {
        RegistrationConfirms.select {
            RegistrationConfirms.confirmToken eq confirmToken
        }.singleOrNull()?.let { RegistrationConfirm(it) }
    }

    fun deleteToken(confirmToken: String): Boolean = transaction {
        RegistrationConfirms.deleteWhere(1) {
            RegistrationConfirms.confirmToken eq confirmToken
        } == 1
    }
}