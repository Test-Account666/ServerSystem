package me.testaccount666.serversystem.managers.database.economy;

import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.database.AbstractDatabaseManager;

/**
 * Abstract base class for economy database managers.
 * Extends AbstractDatabaseManager to provide economy-specific functionality.
 */
public abstract class AbstractEconomyDatabaseManager extends AbstractDatabaseManager {

    /**
     * Creates a new AbstractEconomyDatabaseManager with the specified configuration.
     *
     * @param configReader The configuration reader to get database settings from
     */
    public AbstractEconomyDatabaseManager(ConfigReader configReader) {
        super(configReader, "economy");
    }
}
