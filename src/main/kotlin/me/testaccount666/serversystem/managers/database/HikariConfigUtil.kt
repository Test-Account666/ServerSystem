package me.testaccount666.serversystem.managers.database

import com.zaxxer.hikari.HikariConfig
import me.testaccount666.serversystem.managers.config.ConfigReader
import java.io.File

/**
 * Utility class for configuring HikariCP for different database types.
 */
object HikariConfigUtil {
    /**
     * Configures HikariCP for MySQL.
     * 
     * @param configReader The configuration reader to get database settings from
     * @param configPrefix The prefix for configuration keys (e.g., "Economy.MySQL" or "Moderation.MySQL")
     * @param poolName     The name of the connection pool
     * @return A configured HikariConfig for MySQL
     */
    fun configureMySql(configReader: ConfigReader, configPrefix: String, poolName: String): HikariConfig {
        val host = configReader.getString("${configPrefix}.Host")
        val port = configReader.getInt("${configPrefix}.Port")
        val database = configReader.getString("${configPrefix}.Database")
        val username = configReader.getString("${configPrefix}.Username")
        val password = configReader.getString("${configPrefix}.Password")
        val useSSL = configReader.getBoolean("${configPrefix}.UseSSL")

        val maxPoolSize = configReader.getInt("${configPrefix}.Pool.MaxSize", 10)
        val minIdle = configReader.getInt("${configPrefix}.Pool.MinIdle", 5)
        val maxLifetime = configReader.getLong("${configPrefix}.Pool.MaxLifetime", 30) * 60000 // Minutes
        val connectionTimeout = configReader.getLong("${configPrefix}.Pool.ConnectionTimeout", 30) * 1000 // Seconds
        val idleTimeout = configReader.getLong("${configPrefix}.Pool.IdleTimeout", 600) * 1000 // Seconds

        val jdbcUrl = "jdbc:mysql://${host}:${port}/${database}?useSSL=${useSSL}&autoReconnect=true"

        val config = HikariConfig()
        config.jdbcUrl = jdbcUrl
        config.username = username
        config.password = password
        config.poolName = poolName

        config.maximumPoolSize = maxPoolSize
        config.minimumIdle = minIdle
        config.maxLifetime = maxLifetime
        config.connectionTimeout = connectionTimeout
        config.idleTimeout = idleTimeout

        config.connectionTestQuery = "SELECT 1"

        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        config.addDataSourceProperty("useServerPrepStmts", "true")
        config.addDataSourceProperty("rewriteBatchedStatements", "true")

        return config
    }

    /**
     * Configures HikariCP for SQLite.
     * 
     * @param databaseFile The SQLite database file
     * @param poolName     The name of the connection pool
     * @return A configured HikariConfig for SQLite
     */
    fun configureSqlite(databaseFile: File, poolName: String): HikariConfig {
        val jdbcUrl = "jdbc:sqlite:${databaseFile.absolutePath}"

        val config = HikariConfig()
        config.jdbcUrl = jdbcUrl
        config.poolName = poolName

        config.maximumPoolSize = 1 // Sqlite doesn't support >1
        config.connectionTimeout = 30000 // 30 seconds
        config.idleTimeout = 600000 // 10 minutes

        config.minimumIdle = 1

        config.connectionTestQuery = "SELECT 1"

        config.addDataSourceProperty("foreign_keys", "true")
        config.addDataSourceProperty("journal_mode", "WAL")
        config.addDataSourceProperty("busy_timeout", "5000")
        config.addDataSourceProperty("synchronous", "NORMAL")

        return config
    }
}