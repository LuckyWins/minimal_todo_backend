package ru.appcreators.data.remote

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.slf4j.LoggerFactory
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun connect() {
        log.info("Initialising database")
        val pool = hikari()
        Database.connect(pool)
//        runFlyway(pool)
    }

    fun hikari(): HikariDataSource {
        val config = HikariConfig("/datasource.properties").apply {
//            driverClassName = "oracle.jdbc.OracleDriver"
        }
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(
        block: suspend () -> T
    ): T =
        newSuspendedTransaction { block() }

}