package me.testaccount666.serversystem.managers.database.economy

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.managers.config.ConfigReader
import me.testaccount666.serversystem.managers.database.HikariConfigUtil

/**
 * Manages MySQL database connections for economy-related functionality.
 * Provides a connection pool using HikariCP.
 */
class MySqlEconomyDatabaseManager
/**
 * Creates a new MySqlEconomyDatabaseManager with the specified configuration.
 * 
 * @param configReader The configuration reader to get database settings from
 */
    (configReader: ConfigReader?) : AbstractSqlEconomyDatabaseManager(configReader) {
    /**
     * Initializes the database connection pool.
     */
    override fun initializeConnection() {
        val isClosed = dataSource?.isClosed ?: true
        if (!isClosed) return

        val reader = configReader
        if (reader == null) {
            log.warning("Failed to initialize MySQL connection pool for $databaseType: No configuration found.")
            return
        }

        val config = HikariConfigUtil.configureMySql(reader, "Economy.MySQL", "economy-hikari-pool")
        initializeConnection(config)
    }
}
