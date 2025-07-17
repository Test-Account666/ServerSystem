package me.testaccount666.serversystem.managers.database.moderation;

import com.zaxxer.hikari.HikariDataSource;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.database.HikariConfigUtil;

import java.sql.SQLException;

/**
 * Manages MySQL database connections for moderation-related functionality.
 * Provides a connection pool using HikariCP.
 */
public class MySqlModerationDatabaseManager extends AbstractModerationDatabaseManager {

    /**
     * Creates a new MySqlModerationDatabaseManager with the specified configuration.
     *
     * @param configReader The configuration reader to get database settings from
     */
    public MySqlModerationDatabaseManager(ConfigReader configReader) {
        super(configReader);
    }

    /**
     * Initializes the database connection pool.
     */
    @Override
    protected void initializeConnection() {
        if (dataSource != null && !dataSource.isClosed()) return;

        var config = HikariConfigUtil.configureMySql(configReader, "Moderation.MySQL", "moderation-hikari-pool");
        dataSource = new HikariDataSource(config);
        ServerSystem.getLog().info("Successfully initialized MySQL connection pool for moderation.");
    }

    /**
     * Creates the necessary database tables if they don't exist.
     *
     * @throws SQLException If a database access error occurs
     */
    @Override
    protected void createTablesIfNotExist() throws SQLException {
        try (var connection = getConnection();
             var statement = connection.createStatement()) {
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS Moderation (
                            ID INT AUTO_INCREMENT PRIMARY KEY,
                            TargetUUID VARCHAR(36) NOT NULL,
                            SenderUUID VARCHAR(36) NOT NULL,
                            IssueTime BIGINT NOT NULL,
                            ExpireTime BIGINT NOT NULL,
                            Reason TEXT NOT NULL,
                            Type VARCHAR(20) NOT NULL,
                            INDEX idx_target (TargetUUID),
                            INDEX idx_type (Type)
                        )
                    """);
        }
    }
}
