package ru.appcreators.repositories

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object CatalogTable : IntIdTable("catalog") {
    val ownerId = integer("owner_id").references(Users.id)
    val title = text("title")
}

data class Catalog(
    val id: Int,
    val ownerId: Int,
    val title: String,
) {
    // from ResultRow
    constructor(resultRow: ResultRow) : this(
        id = resultRow[CatalogTable.id].value,
        ownerId = resultRow[CatalogTable.ownerId],
        title = resultRow[CatalogTable.title],
    )
}

class CatalogRepository {

    fun getCatalog(
        userId: Int,
        // TODO: get shared items
        own: Boolean,
    ): List<Catalog> = transaction {
        val query = CatalogTable
            .select {
                CatalogTable.ownerId eq userId
            }
        query.map {
            Catalog(it)
        }
    }

    fun getCatalogSingle(id: Int): Catalog? = transaction {
        CatalogTable
            .select {
                CatalogTable.id eq id
            }
            .singleOrNull()?.let { Catalog(it) }
    }

    /**
     * return [Int] - inserted catalog id
     */
    fun addCatalog(
        userId: Int,
        title: String,
    ): Int = transaction {
        CatalogTable
            .insertAndGetId {
                it[CatalogTable.ownerId] = userId
                it[CatalogTable.title] = title
            }.value
    }

    /**
     * batch insert
     * return count on inserted medias
     */
//    fun addCatalog(
//        userId: Int,
//        catalog: List<Catalog>,
//    ): Int = transaction {
//        CatalogTable.batchInsert(catalog,
//            ignore = true,
//            shouldReturnGeneratedValues = false,
//            body = { item ->
//                this[CatalogTable.ownerId] = userId
//                this[CatalogTable.title] = item.title
//        }).count()
//    }

    fun deleteCatalog(id: Int): Boolean = transaction {
        CatalogTable.deleteWhere {
            CatalogTable.id.eq(id)
        } == 1
    }

}