package me.testaccount666.serversystem.managers.database.economy

import me.testaccount666.serversystem.managers.database.HikariConfigUtil
import java.io.File
import java.nio.file.Path

/**
 * Manages SQLite database connections for economy-related functionality.
 * Provides a connection pool using HikariCP.
 */
class SqliteEconomyDatabaseManager(dataFolder: File) : AbstractSqlEconomyDatabaseManager(null) {
    private val _databaseFile: File = Path.of(dataFolder.path, "data", "economy.db").toFile()

    /**
     * Initializes the database connection pool.
     */
    override fun initializeConnection() {
        val isClosed = dataSource?.isClosed ?: true
        if (!isClosed) return

        if (!_databaseFile.parentFile.exists()) _databaseFile.parentFile.mkdirs()

        val config = HikariConfigUtil.configureSqlite(_databaseFile, "economy-sqlite-pool")
        initializeConnection(config)
    }
}
