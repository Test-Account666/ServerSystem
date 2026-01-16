package me.testaccount666.serversystem.managers.database

import me.testaccount666.serversystem.managers.config.ConfigReader

/**
 * Interface for all database managers.
 */
interface DatabaseManager {
    val configReader: ConfigReader?
    val databaseType: String

    /**
     * Initializes the database connection and creates tables.
     */
    fun initialize()

    /**
     * Closes the database connection.
     */
    fun shutdown()
}
