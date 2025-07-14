package me.testaccount666.serversystem.managers.database.economy;

import com.zaxxer.hikari.HikariDataSource;
import me.testaccount666.serversystem.managers.database.HikariConfigUtil;
import org.bukkit.Bukkit;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;

/**
 * Manages SQLite database connections for economy-related functionality.
 * Provides a connection pool using HikariCP.
 */
public class SqliteEconomyDatabaseManager extends AbstractEconomyDatabaseManager {
    private final File _databaseFile;

    /**
     * Creates a new SqliteEconomyDatabaseManager with the specified configuration.
     *
     * @param dataFolder The plugin's data folder where the SQLite database file will be stored
     */
    public SqliteEconomyDatabaseManager(File dataFolder) {
        super(null);

        _databaseFile = Path.of(dataFolder.getPath(), "data", "economy.db").toFile();
    }

    @Override
    public void initialize() {
        try {
            if (!_databaseFile.getParentFile().exists()) _databaseFile.getParentFile().mkdirs();

            super.initialize();
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to initialize SQLite economy database: ${e.getMessage()}");
            e.printStackTrace();
        }
    }

    /**
     * Initializes the database connection pool.
     */
    @Override
    protected void initializeConnection() {
        if (dataSource != null && !dataSource.isClosed()) return;

        var config = HikariConfigUtil.configureSqlite(_databaseFile, "economy-sqlite-pool");
        dataSource = new HikariDataSource(config);

        Bukkit.getLogger().info("Successfully initialized SQLite connection pool for economy.");
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
