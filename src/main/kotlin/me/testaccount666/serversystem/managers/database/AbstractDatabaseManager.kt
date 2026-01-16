package me.testaccount666.serversystem.managers.database

import me.testaccount666.serversystem.managers.config.ConfigReader

/**
 * Abstract base class for all database managers.
 */
abstract class AbstractDatabaseManager(
    override val configReader: ConfigReader?,
    override val databaseType: String
) : DatabaseManager {
    /**
     * Initializes the database connection and creates tables.
     */
    abstract override fun initialize()

    /**
     * Closes the database connection.
     */
    abstract override fun shutdown()
}