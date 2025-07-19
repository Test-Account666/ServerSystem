package me.testaccount666.serversystem.managers.database;

import com.zaxxer.hikari.HikariDataSource;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.config.ConfigReader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Abstract base class for all database managers.
 * Provides common functionality for database connection management using HikariCP.
 */
public abstract class AbstractDatabaseManager {
    protected final ConfigReader configReader;
    protected final String databaseType;
    protected HikariDataSource dataSource;

    /**
     * Creates a new AbstractDatabaseManager with the specified configuration.
     *
     * @param configReader The configuration reader to get database settings from
     * @param databaseType The type of database (e.g., "economy", "moderation")
     */
    public AbstractDatabaseManager(ConfigReader configReader, String databaseType) {
        this.configReader = configReader;
        this.databaseType = databaseType;
    }

    /**
     * Initializes the database connection and creates tables.
     */
    public void initialize() {
        try {
            initializeConnection();
            createTablesIfNotExist();
        } catch (SQLException exception) {
            ServerSystem.getLog().log(Level.SEVERE, "Failed to initialize ${databaseType} database", exception);
        }
    }

    /**
     * Initializes the database connection pool.
     *
     * @throws SQLException If a database access error occurs
     */
    protected abstract void initializeConnection() throws SQLException;

    /**
     * Creates the necessary database tables if they don't exist.
     *
     * @throws SQLException If a database access error occurs
     */
    protected abstract void createTablesIfNotExist() throws SQLException;

    /**
     * Gets a connection from the connection pool.
     *
     * @return A database connection from the pool
     * @throws SQLException If a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) initializeConnection();
        return dataSource.getConnection();
    }

    /**
     * Closes the database connection pool.
     */
    public void shutdown() {
        try {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
                ServerSystem.getLog().info("${databaseType} database connection pool closed.");
            }
        } catch (Exception exception) {
            ServerSystem.getLog().log(Level.SEVERE, "Error closing ${databaseType} database connection pool", exception);
        }
    }
}