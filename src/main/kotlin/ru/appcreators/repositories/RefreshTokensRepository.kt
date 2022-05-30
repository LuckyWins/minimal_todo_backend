package ru.appcreators.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object RefreshTokens : Table("refresh_tokens") {
    val userId = integer("user_id").references(Users.id)
    val refreshToken = varchar("refresh_token", 300)
    val expiresAt = long("expires_at")
}

data class RefreshToken(
    val userId: Int,
    val refreshToken: String,
    val expiresAt: Long
) {
    constructor(resultRow: ResultRow) : this(
        userId = resultRow[RefreshTokens.userId],
        refreshToken = resultRow[RefreshTokens.refreshToken],
        expiresAt = resultRow[RefreshTokens.expiresAt]
    )
}

class RefreshTokensRepository {

    fun getToken(refreshToken: String): RefreshToken? = transaction {
        RefreshTokens.select {
            RefreshTokens.refreshToken eq refreshToken
        }.singleOrNull()?.let { RefreshToken(it) }
    }

    fun insertToken(userId: Int, refreshToken: String, expiresAt: Long): Boolean = transaction {
        RefreshTokens.insert {
            it[RefreshTokens.userId] = userId
            it[RefreshTokens.refreshToken] = refreshToken
            it[RefreshTokens.expiresAt] = expiresAt
        }.insertedCount == 1
    }

    fun updateToken(refreshToken: String, newRefreshToken: String, expiresAt: Long): Boolean = transaction {
        RefreshTokens.update({RefreshTokens.refreshToken eq refreshToken}) {
            it[RefreshTokens.refreshToken] = newRefreshToken
            it[RefreshTokens.expiresAt] = expiresAt
        } == 1
    }
}