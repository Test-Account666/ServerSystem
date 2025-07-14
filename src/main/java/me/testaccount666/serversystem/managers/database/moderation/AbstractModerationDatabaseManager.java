package me.testaccount666.serversystem.managers.database.moderation;

import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.database.AbstractDatabaseManager;

/**
 * Abstract base class for moderation database managers.
 * Extends AbstractDatabaseManager to provide moderation-specific functionality.
 */
public abstract class AbstractModerationDatabaseManager extends AbstractDatabaseManager {

    /**
     * Creates a new AbstractModerationDatabaseManager with the specified configuration.
     *
     * @param configReader The configuration reader to get database settings from
     */
    public AbstractModerationDatabaseManager(ConfigReader configReader) {
        super(configReader, "moderation");
    }
}
