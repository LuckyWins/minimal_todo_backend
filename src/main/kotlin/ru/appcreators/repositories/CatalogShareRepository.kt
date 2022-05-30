package ru.appcreators.repositories

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object CatalogShareTable : Table("catalog_share") {
    val userId = integer("user_id").references(Users.id)
    val catalogId = integer("catalog_id").references(CatalogTable.id)
}

class CatalogShareRepository {
    fun isShareExists(
        userId: Int,
        catalogId: Int,
    ): Boolean = transaction {
        CatalogShareTable
            .select {
                CatalogShareTable.catalogId.eq(catalogId) and CatalogShareTable.userId.eq(userId)
            }.count() == 1L
    }

    fun addShare(
        userId: Int,
        catalogId: Int,
    ): Boolean = transaction {
        CatalogShareTable
            .insert {
                it[CatalogShareTable.userId] = userId
                it[CatalogShareTable.catalogId] = catalogId
            }.insertedCount == 1
    }
}