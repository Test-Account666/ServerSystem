package me.testaccount666.serversystem.managers.database.moderation;

import org.bukkit.Bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages SQLite database connections for moderation-related functionality.
 * Provides a shared SQLite connection instance and keeps it alive.
 */
public class SqliteModerationDatabaseManager extends AbstractModerationDatabaseManager {
    private static final int _KEEP_ALIVE_INTERVAL_SECONDS = 60 * 30;
    private static final String _KEEP_ALIVE_QUERY = "SELECT 1";

    private final File _databaseFile;
    private final ScheduledExecutorService _keepAliveScheduler;
    private Connection _connection;

    /**
     * Creates a new SqliteModerationDatabaseManager with the specified configuration.
     *
     * @param dataFolder The plugin's data folder where the SQLite database file will be stored
     */
    public SqliteModerationDatabaseManager(File dataFolder) {
        super(null);

        // Create the database file in the plugin's data folder
        _databaseFile = new File(dataFolder, "moderation.db");

        _keepAliveScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void initialize() {
        try {
            // Ensure the parent directory exists
            if (!_databaseFile.getParentFile().exists()) _databaseFile.getParentFile().mkdirs();

            initializeConnection();
            setupKeepAlive();
            createTablesIfNotExist();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to initialize SQLite moderation database connection: ${e.getMessage()}");
            e.printStackTrace();
        }
    }

    /**
     * Initializes the database connection.
     *
     * @throws SQLException If a database access error occurs
     */
    private void initializeConnection() throws SQLException {
        if (_connection != null && !_connection.isClosed()) return;

        var url = "jdbc:sqlite:" + _databaseFile.getAbsolutePath();

        _connection = DriverManager.getConnection(url);

        // Enable foreign keys in SQLite
        try (var statement = _connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        Bukkit.getLogger().info("Successfully connected to SQLite database for moderation.");
    }

    /**
     * Sets up a scheduled task to keep the connection alive.
     */
    private void setupKeepAlive() {
        _keepAliveScheduler.scheduleAtFixedRate(() -> {
            try {
                if (_connection == null || _connection.isClosed()) {
                    initializeConnection();
                    return;
                }

                try (var statement = _connection.createStatement()) {
                    statement.execute(_KEEP_ALIVE_QUERY);
                }

                Bukkit.getLogger().fine("SQLite moderation database keep-alive ping successful.");
            } catch (SQLException e) {
                Bukkit.getLogger().warning("SQLite moderation database keep-alive ping failed: ${e.getMessage()}");
                try {
                    initializeConnection();
                } catch (SQLException reconnectEx) {
                    Bukkit.getLogger().severe("Failed to reconnect to SQLite moderation database: ${reconnectEx.getMessage()}");
                }
            }
        }, _KEEP_ALIVE_INTERVAL_SECONDS, _KEEP_ALIVE_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Creates the necessary database tables if they don't exist.
     * Note: SQLite syntax differs from MySQL for auto-increment and indexes.
     *
     * @throws SQLException If a database access error occurs
     */
    private void createTablesIfNotExist() throws SQLException {
        try (var statement = _connection.createStatement()) {
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

            // Create indexes separately
            statement.execute("CREATE INDEX IF NOT EXISTS idx_target ON Moderation (TargetUUID)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_type ON Moderation (Type)");
        }
    }

    /**
     * Gets the shared database connection.
     * If the connection is closed, it will be reopened.
     *
     * @return The database connection
     * @throws SQLException If a database access error occurs
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (_connection == null || _connection.isClosed()) initializeConnection();
        return _connection;
    }

    /**
     * Closes the database connection and shuts down the keep-alive scheduler.
     */
    @Override
    public void shutdown() {
        if (!_keepAliveScheduler.isShutdown() && !_keepAliveScheduler.isTerminated()) _keepAliveScheduler.shutdown();

        try {
            if (_connection != null && !_connection.isClosed()) {
                _connection.close();
                Bukkit.getLogger().info("SQLite moderation database connection closed.");
            }
        } catch (SQLException exception) {
            Bukkit.getLogger().warning("Error closing SQLite moderation database connection:");
            exception.printStackTrace();
        }
    }
}