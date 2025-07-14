package me.testaccount666.serversystem.managers.database.economy;

import com.zaxxer.hikari.HikariDataSource;
import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.database.HikariConfigUtil;
import org.bukkit.Bukkit;

import java.sql.SQLException;

/**
 * Manages MySQL database connections for economy-related functionality.
 * Provides a connection pool using HikariCP.
 */
public class MySqlEconomyDatabaseManager extends AbstractEconomyDatabaseManager {

    /**
     * Creates a new MySqlEconomyDatabaseManager with the specified configuration.
     *
     * @param configReader The configuration reader to get database settings from
     */
    public MySqlEconomyDatabaseManager(ConfigReader configReader) {
        super(configReader);
    }

    /**
     * Initializes the database connection pool.
     */
    @Override
    protected void initializeConnection() {
        if (dataSource != null && !dataSource.isClosed()) return;

        var config = HikariConfigUtil.configureMySql(configReader, "Economy.MySQL", "economy-hikari-pool");
        dataSource = new HikariDataSource(config);
        Bukkit.getLogger().info("Successfully initialized MySQL connection pool for economy.");
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
                        CREATE TABLE IF NOT EXISTS Economy (
                            Owner VARCHAR(36) NOT NULL,
                            Balance DECIMAL(65,2) NOT NULL,
                            AccountId VARCHAR(36) NOT NULL,
                            PRIMARY KEY (Owner, AccountId)
                        )
                    """);

            statement.execute("CREATE INDEX IF NOT EXISTS idx_owner ON Economy (Owner)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_account_id ON Economy (AccountId)");
        }
    }
}
