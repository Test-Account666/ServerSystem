package me.testaccount666.serversystem.managers.database.moderation;

import me.testaccount666.serversystem.managers.config.ConfigReader;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractModerationDatabaseManager {
    public AbstractModerationDatabaseManager(ConfigReader configReader) {
    }

    public abstract Connection getConnection() throws SQLException;

    public abstract void initialize();

    public abstract void shutdown();
}
