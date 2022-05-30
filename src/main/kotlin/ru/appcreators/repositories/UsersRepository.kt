package ru.appcreators.repositories

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Users : IntIdTable("users") {
    val name = varchar("name", 255).uniqueIndex()
    val ip = varchar("ip", 255).nullable()
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val type = integer("type")
    val confirmed = bool("confirmed")
}

data class User(
    val id: Int,
    val name: String,
    val ip: String?,
    val email: String,
    val password: String,
    val type: Int,
    val confirmed: Boolean,
) {
    // from ResultRow
    constructor(resultRow: ResultRow) : this(
        id = resultRow[Users.id].value,
        name = resultRow[Users.name],
        ip = resultRow[Users.ip],
        email = resultRow[Users.email],
        password = resultRow[Users.password],
        type = resultRow[Users.type],
        confirmed = resultRow[Users.confirmed]
    )
}

class UsersRepository {

    fun getUserById(id: Int): User? = transaction {
        val user = Users.select {
            Users.id eq id
        }.singleOrNull()
        return@transaction user?.let { User(it) }
    }

    fun getUserByName(username: String): User? = transaction {
        val user = Users.select {
            Users.name.eq(username)
        }.singleOrNull()
        return@transaction user?.let { User(it) }
    }

    fun getUserByEmail(email: String): User? = transaction {
        val user = Users.select {
            Users.email.eq(email)
        }.singleOrNull()
        return@transaction user?.let { User(it) }
    }

    fun createUser(username: String, ip: String, email: String, password: String): Boolean = transaction {
        Users.insert {
            it[Users.name] = username
            it[Users.ip] = ip
            it[Users.email] = email
            it[Users.password] = password
        }.insertedCount == 1
    }

    fun updateIp(userId: Int, ip: String): Boolean = transaction {
        Users.update({Users.id eq userId}) {
            it[Users.ip] = ip
        } == 1
    }

    fun confirmUser(id: Int): Boolean = transaction {
        Users.update({Users.id eq id}) {
            it[Users.confirmed] = true
        } == 1
    }

}