package me.testaccount666.serversystem.managers.database.moderation

import me.testaccount666.serversystem.managers.config.ConfigReader
import me.testaccount666.serversystem.managers.database.AbstractSqlDatabaseManager
import java.sql.SQLException

/**
 * Abstract base class for SQL-based moderation database managers.
 * Provides common table creation logic with hooks for platform-specific syntax.
 */
abstract class AbstractSqlModerationDatabaseManager(configReader: ConfigReader?) : AbstractSqlDatabaseManager(configReader, "moderation"),
    ModerationDatabaseManager {

    /**
     * The SQL fragment for an auto-incrementing primary key.
     */
    protected abstract val autoIncrementSql: String

    /**
     * Whether the platform supports inline index declarations in CREATE TABLE.
     */
    protected open val supportsInlineIndex: Boolean = false

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
                        CREATE TABLE IF NOT EXISTS Moderation (
                            ID $autoIncrementSql,
                            TargetUUID VARCHAR(36) NOT NULL,
                            SenderUUID VARCHAR(36) NOT NULL,
                            IssueTime BIGINT NOT NULL,
                            ExpireTime BIGINT NOT NULL,
                            Reason TEXT NOT NULL,
                            Type VARCHAR(20) NOT NULL
                            ${if (supportsInlineIndex) ", INDEX idx_target (TargetUUID), INDEX idx_type (Type)" else ""}
                        )
                    
                    """.trimIndent()
                )

                if (!supportsInlineIndex) {
                    statement.execute("CREATE INDEX IF NOT EXISTS idx_target ON Moderation (TargetUUID)")
                    statement.execute("CREATE INDEX IF NOT EXISTS idx_type ON Moderation (Type)")
                }
            }
        }
    }
}
