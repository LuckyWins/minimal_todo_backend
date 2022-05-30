package ru.appcreators.repositories

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object CatalogShareSecrets : Table("catalog_share_secret") {
    val shareSecret = varchar("share_secret", 255).uniqueIndex()
    val catalogId = integer("catalog_id").references(CatalogTable.id)
}

class CatalogShareSecretRepository {
    fun getSecret(
        catalogId: Int,
    ): String? = transaction {
        CatalogShareSecrets
            .select {
                CatalogShareSecrets.catalogId eq catalogId
            }
            .singleOrNull()?.let { it[CatalogShareSecrets.shareSecret] }
    }

    fun addShareSecret(
        shareSecret: String,
        catalogId: Int,
    ): Boolean = transaction {
        CatalogShareSecrets
            .insert {
                it[CatalogShareSecrets.shareSecret] = shareSecret
                it[CatalogShareSecrets.catalogId] = catalogId
            }.insertedCount == 1
    }
}