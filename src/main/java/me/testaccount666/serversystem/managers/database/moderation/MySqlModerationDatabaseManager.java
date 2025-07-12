package me.testaccount666.serversystem.managers.database.moderation;

import me.testaccount666.serversystem.managers.config.ConfigReader;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages database connections for moderation-related functionality.
 * Provides a shared MySQL connection instance and keeps it alive.
 */
public class MySqlModerationDatabaseManager extends AbstractModerationDatabaseManager {
    private static final int _KEEP_ALIVE_INTERVAL_SECONDS = 60 * 30;
    private static final String _KEEP_ALIVE_QUERY = "SELECT 1";

    private final String _host;
    private final int _port;
    private final String _database;
    private final String _username;
    private final String _password;
    private final boolean _useSSL;
    private final ScheduledExecutorService _keepAliveScheduler;
    private Connection _connection;

    /**
     * Creates a new ModerationDatabaseManager with the specified configuration.
     *
     * @param configReader The configuration reader to get database settings from
     */
    public MySqlModerationDatabaseManager(ConfigReader configReader) {
        super(configReader);

        _host = configReader.getString("Moderation.MySQL.Host");
        _port = configReader.getInt("Moderation.MySQL.Port");
        _database = configReader.getString("Moderation.MySQL.Database");
        _username = configReader.getString("Moderation.MySQL.Username");
        _password = configReader.getString("Moderation.MySQL.Password");
        _useSSL = configReader.getBoolean("Moderation.MySQL.UseSSL");

        _keepAliveScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void initialize() {
        try {
            initializeConnection();
            setupKeepAlive();
            createTablesIfNotExist();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to initialize moderation database connection: ${e.getMessage()}");
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

        var url = "jdbc:mysql://${_host}:${_port}/${_database}?useSSL=${_useSSL}&autoReconnect=true";

        _connection = DriverManager.getConnection(url, _username, _password);
        Bukkit.getLogger().info("Successfully connected to MySQL database for moderation.");
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

                Bukkit.getLogger().fine("Moderation database keep-alive ping successful.");
            } catch (SQLException e) {
                Bukkit.getLogger().warning("Moderation database keep-alive ping failed: ${e.getMessage()}");
                try {
                    initializeConnection();
                } catch (SQLException reconnectEx) {
                    Bukkit.getLogger().severe("Failed to reconnect to moderation database: ${reconnectEx.getMessage()}");
                }
            }
        }, _KEEP_ALIVE_INTERVAL_SECONDS, _KEEP_ALIVE_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Creates the necessary database tables if they don't exist.
     *
     * @throws SQLException If a database access error occurs
     */
    private void createTablesIfNotExist() throws SQLException {
        try (var statement = _connection.createStatement()) {
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
                Bukkit.getLogger().info("Moderation database connection closed.");
            }
        } catch (SQLException exception) {
            Bukkit.getLogger().warning("Error closing moderation database connection:");
            exception.printStackTrace();
        }
    }
}