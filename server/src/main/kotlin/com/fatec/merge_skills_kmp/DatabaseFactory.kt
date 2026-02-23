package com.fatec.merge_skills_kmp

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun init() {
        logger.info("Initializing Database...")
        
        val url = System.getProperty("JDBC_DATABASE_URL") ?: System.getenv("JDBC_DATABASE_URL")
        val user = System.getProperty("JDBC_DATABASE_USER") ?: System.getenv("JDBC_DATABASE_USER")
        val password = System.getProperty("JDBC_DATABASE_PASSWORD") ?: System.getenv("JDBC_DATABASE_PASSWORD")

        if (url == null) {
            logger.warn("JDBC_DATABASE_URL not found. Database features will be disabled.")
            return
        }

        val database = Database.connect(createHikariDataSource(url, user, password))
        
        transaction(database) {
            logger.info("Database connection established and pool verified.")
        }
    }

    private fun createHikariDataSource(url: String, user: String?, pass: String?): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = url
            user?.let { username = it }
            pass?.let { password = it }
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }
}
