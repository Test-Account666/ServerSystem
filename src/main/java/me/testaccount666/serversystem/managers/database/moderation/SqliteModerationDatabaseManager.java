package me.testaccount666.serversystem.managers.database.moderation;

import com.zaxxer.hikari.HikariDataSource;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.database.HikariConfigUtil;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Manages SQLite database connections for moderation-related functionality.
 * Provides a connection pool using HikariCP.
 */
public class SqliteModerationDatabaseManager extends AbstractModerationDatabaseManager {
    private final File _databaseFile;

    /**
     * Creates a new SqliteModerationDatabaseManager with the specified configuration.
     *
     * @param dataFolder The plugin's data folder where the SQLite database file will be stored
     */
    public SqliteModerationDatabaseManager(File dataFolder) {
        super(null);

        _databaseFile = Path.of(dataFolder.getPath(), "data", "moderation.db").toFile();
    }

    @Override
    public void initialize() {
        try {
            if (!_databaseFile.getParentFile().exists()) _databaseFile.getParentFile().mkdirs();

            super.initialize();
        } catch (Exception exception) {
            ServerSystem.getLog().log(Level.SEVERE, "Failed to initialize SQLite moderation database", exception);
        }
    }

    /**
     * Initializes the database connection pool.
     */
    @Override
    protected void initializeConnection() {
        if (dataSource != null && !dataSource.isClosed()) return;

        var config = HikariConfigUtil.configureSqlite(_databaseFile, "moderation-sqlite-pool");
        dataSource = new HikariDataSource(config);

        ServerSystem.getLog().info("Successfully initialized SQLite connection pool for moderation.");
    }

    /**
     * Creates the necessary database tables if they don't exist.
     * Note: SQLite syntax differs from MySQL for auto-increment and indexes.
     *
     * @throws SQLException If a database access error occurs
     */
    @Override
    protected void createTablesIfNotExist() throws SQLException {
        try (var connection = getConnection();
             var statement = connection.createStatement()) {
            // SQLite uses INTEGER PRIMARY KEY AUTOINCREMENT instead of AUTO_INCREMENT
            // And CREATE INDEX statements instead of inline INDEX declarations
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS Moderation (
                            ID INTEGER PRIMARY KEY AUTOINCREMENT,
                            TargetUUID VARCHAR(36) NOT NULL,
                            SenderUUID VARCHAR(36) NOT NULL,
                            IssueTime BIGINT NOT NULL,
                            ExpireTime BIGINT NOT NULL,
                            Reason TEXT NOT NULL,
                            Type VARCHAR(20) NOT NULL
                        )
                    """);

            statement.execute("CREATE INDEX IF NOT EXISTS idx_target ON Moderation (TargetUUID)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_type ON Moderation (Type)");
        }
    }
}
