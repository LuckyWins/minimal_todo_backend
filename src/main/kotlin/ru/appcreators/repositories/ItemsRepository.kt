package ru.appcreators.repositories

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object ItemsTable : IntIdTable("items") {
    val catalogId = integer("catalog_id").references(CatalogTable.id)
    val done = bool("done")
    val value = text("value").nullable()
}

data class Item(
    val id: Int,
    val catalogId: Int,
    val done: Boolean,
    val value: String?,
    // for check owner
    val ownerId: Int? = null
) {
    // from ResultRow
    constructor(resultRow: ResultRow, ownerId: Int? = null) : this(
        id = resultRow[ItemsTable.id].value,
        catalogId = resultRow[ItemsTable.catalogId],
        done = resultRow[ItemsTable.done],
        value = resultRow[ItemsTable.value],
        ownerId = ownerId
    )
}

class ItemsRepository {
    /**
     * with ownerId
     */
    fun getItem(id: Int): Item? = transaction {
        ItemsTable
            .innerJoin(CatalogTable)
            .slice(buildList {
                addAll(ItemsTable.columns)
                add(CatalogTable.ownerId)
            })
            .select {
                ItemsTable.id eq id and (ItemsTable.catalogId eq CatalogTable.id)
            }
            .singleOrNull()?.let { Item(it, it[CatalogTable.ownerId]) }
    }

    /**
     * return [Int] - inserted item id
     */
    fun addItem(
        catalogId: Int,
        done: Boolean?,
        value: String?,
    ): Int = transaction {
        ItemsTable
            .insertAndGetId {
                it[ItemsTable.catalogId] = catalogId
                done?.let { doneNotNull -> it[ItemsTable.done] = doneNotNull }
                it[ItemsTable.value] = value
            }.value
    }

    fun deleteItem(id: Int): Boolean = transaction {
        ItemsTable.deleteWhere {
            ItemsTable.id.eq(id)
        } == 1
    }

    fun updateItem(
        id: Int,
        done: Boolean?,
        value: String?
    ): Boolean = transaction {
        ItemsTable.update({ItemsTable.id eq id}) {
            done?.let { doneNotNull -> it[ItemsTable.done] = doneNotNull }
            value?.let { valueNotNull -> it[ItemsTable.value] = valueNotNull }
        } == 1
    }
}