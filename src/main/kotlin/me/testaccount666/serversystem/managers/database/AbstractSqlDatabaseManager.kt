package me.testaccount666.serversystem.managers.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.managers.config.ConfigReader
import java.sql.Connection
import java.sql.SQLException
import java.util.logging.Level

/**
 * Abstract base class for SQL-based database managers.
 * Provides common functionality for database connection management using HikariCP.
 */
abstract class AbstractSqlDatabaseManager(
    configReader: ConfigReader?,
    databaseType: String
) : AbstractDatabaseManager(configReader, databaseType) {
    protected var dataSource: HikariDataSource? = null

    /**
     * Initializes the database connection and creates tables.
     */
    override fun initialize() {
        try {
            initializeConnection()
            createTablesIfNotExist()
        } catch (exception: SQLException) {
            log.log(Level.SEVERE, "Failed to initialize ${databaseType} database", exception)
        }
    }

    protected abstract fun initializeConnection()

    /**
     * Initializes the database connection pool.
     * 
     * @throws SQLException If a database access error occurs
     */
    protected open fun initializeConnection(config: HikariConfig) {
        dataSource = HikariDataSource(config)
        log.info("Successfully initialized connection pool for $databaseType.")
    }

    /**
     * Creates the necessary database tables if they don't exist.
     * 
     * @throws SQLException If a database access error occurs
     */
    protected abstract fun createTablesIfNotExist()

    val connection: Connection
        /**
         * Gets a connection from the connection pool.
         * 
         * @return A database connection from the pool
         * @throws SQLException If a database access error occurs
         */
        get() {
            val isClosed = dataSource?.isClosed ?: true

            if (isClosed) initializeConnection()
            return dataSource!!.connection
        }

    /**
     * Closes the database connection pool.
     */
    override fun shutdown() {
        try {
            val isClosed = dataSource?.isClosed ?: true
            if (!isClosed) {
                dataSource?.close()
                log.info("${databaseType} database connection pool closed.")
            }
        } catch (exception: Exception) {
            log.log(Level.SEVERE, "Error closing ${databaseType} database connection pool", exception)
        }
    }
}
