package me.testaccount666.serversystem.managers.database.economy

import me.testaccount666.serversystem.managers.config.ConfigReader
import me.testaccount666.serversystem.managers.database.AbstractSqlDatabaseManager
import java.sql.SQLException

/**
 * Abstract base class for SQL-based economy database managers.
 * Provides common table creation logic for both MySQL and SQLite.
 */
abstract class AbstractSqlEconomyDatabaseManager(configReader: ConfigReader?) : AbstractSqlDatabaseManager(configReader, "economy"),
    EconomyDatabaseManager {

    /**
     * Creates the necessary database tables if they don't exist.
     * 
     * @throws SQLException If a database access error occurs
     */
    @Throws(SQLException::class)
    override fun createTablesIfNotExist() {
        connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.execute(
                    """
                        CREATE TABLE IF NOT EXISTS Economy (
                            Owner VARCHAR(36) NOT NULL,
                            Balance DECIMAL(65,2) NOT NULL,
                            AccountId VARCHAR(36) NOT NULL,
                            PRIMARY KEY (Owner, AccountId)
                        )
                    
                    """.trimIndent()
                )
                statement.execute("CREATE INDEX IF NOT EXISTS idx_owner ON Economy (Owner)")
                statement.execute("CREATE INDEX IF NOT EXISTS idx_account_id ON Economy (AccountId)")
            }
        }
    }
}
