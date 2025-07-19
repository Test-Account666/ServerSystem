package me.testaccount666.serversystem.managers.database;

import com.zaxxer.hikari.HikariConfig;
import me.testaccount666.serversystem.managers.config.ConfigReader;

import java.io.File;

/**
 * Utility class for configuring HikariCP for different database types.
 */
public class HikariConfigUtil {

    /**
     * Configures HikariCP for MySQL.
     *
     * @param configReader The configuration reader to get database settings from
     * @param configPrefix The prefix for configuration keys (e.g., "Economy.MySQL" or "Moderation.MySQL")
     * @param poolName     The name of the connection pool
     * @return A configured HikariConfig for MySQL
     */
    public static HikariConfig configureMySql(ConfigReader configReader, String configPrefix, String poolName) {
        var host = configReader.getString("${configPrefix}.Host");
        var port = configReader.getInt("${configPrefix}.Port");
        var database = configReader.getString("${configPrefix}.Database");
        var username = configReader.getString("${configPrefix}.Username");
        var password = configReader.getString("${configPrefix}.Password");
        var useSSL = configReader.getBoolean("${configPrefix}.UseSSL");

        var maxPoolSize = configReader.getInt("${configPrefix}.Pool.MaxSize", 10);
        var minIdle = configReader.getInt("${configPrefix}.Pool.MinIdle", 5);
        var maxLifetime = configReader.getLong("${configPrefix}.Pool.MaxLifetime", 30) * 60000; // Minutes
        var connectionTimeout = configReader.getLong("${configPrefix}.Pool.ConnectionTimeout", 30) * 1000; // Seconds
        var idleTimeout = configReader.getLong("${configPrefix}.Pool.IdleTimeout", 600) * 1000; // Seconds

        var jdbcUrl = "jdbc:mysql://${host}:${port}/${database}?useSSL=${useSSL}&autoReconnect=true";

        var config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setPoolName(poolName);

        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setMaxLifetime(maxLifetime);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);

        config.setConnectionTestQuery("SELECT 1");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");

        return config;
    }

    /**
     * Configures HikariCP for SQLite.
     *
     * @param databaseFile The SQLite database file
     * @param poolName     The name of the connection pool
     * @return A configured HikariConfig for SQLite
     */
    public static HikariConfig configureSqlite(File databaseFile, String poolName) {
        var jdbcUrl = "jdbc:sqlite:${databaseFile.getAbsolutePath()}";

        var config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setPoolName(poolName);

        config.setMaximumPoolSize(1); // Sqlite doesn't support >1
        config.setConnectionTimeout(30000); // 30 seconds
        config.setIdleTimeout(600000); // 10 minutes

        config.setMinimumIdle(1);

        config.setConnectionTestQuery("SELECT 1");

        config.addDataSourceProperty("foreign_keys", "true");
        config.addDataSourceProperty("journal_mode", "WAL");
        config.addDataSourceProperty("busy_timeout", "5000");
        config.addDataSourceProperty("synchronous", "NORMAL");

        return config;
    }
}