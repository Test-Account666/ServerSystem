package me.testaccount666.serversystem.managers.database;

import me.testaccount666.serversystem.managers.config.ConfigReader;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages database connections for the ServerSystem plugin.
 * Provides a shared MySQL connection instance and keeps it alive.
 */
public class DatabaseManager {
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
     * Creates a new DatabaseManager with the specified configuration.
     *
     * @param configReader The configuration reader to get database settings from
     */
    public DatabaseManager(ConfigReader configReader) {
        _host = configReader.getString("Economy.MySQL.Host");
        _port = configReader.getInt("Economy.MySQL.Port");
        _database = configReader.getString("Economy.MySQL.Database");
        _username = configReader.getString("Economy.MySQL.Username");
        _password = configReader.getString("Economy.MySQL.Password");
        _useSSL = configReader.getBoolean("Economy.MySQL.UseSSL");

        _keepAliveScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void initialize() {
        try {
            initializeConnection();
            setupKeepAlive();
            createTablesIfNotExist();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to initialize database connection: " + e.getMessage());
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
        Bukkit.getLogger().info("Successfully connected to MySQL database.");
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

                Bukkit.getLogger().fine("Database keep-alive ping successful.");
            } catch (SQLException e) {
                Bukkit.getLogger().warning("Database keep-alive ping failed: " + e.getMessage());
                try {
                    initializeConnection();
                } catch (SQLException reconnectEx) {
                    Bukkit.getLogger().severe("Failed to reconnect to database: " + reconnectEx.getMessage());
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
                        CREATE TABLE IF NOT EXISTS Economy (
                            Owner VARCHAR(36) PRIMARY KEY,
                            Balance DECIMAL(65,2) NOT NULL,
                            AccountId BIGINT NOT NULL
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
    public Connection getConnection() throws SQLException {
        if (_connection == null || _connection.isClosed()) initializeConnection();
        return _connection;
    }

    /**
     * Closes the database connection and shuts down the keep-alive scheduler.
     */
    public void shutdown() {
        if (!_keepAliveScheduler.isShutdown() && !_keepAliveScheduler.isTerminated()) _keepAliveScheduler.shutdown();

        try {
            if (_connection != null && !_connection.isClosed()) {
                _connection.close();
                Bukkit.getLogger().info("Database connection closed.");
            }
        } catch (SQLException exception) {
            Bukkit.getLogger().warning("Error closing database connection:");
            exception.printStackTrace();
        }
    }
}