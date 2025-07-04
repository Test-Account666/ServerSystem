package me.testaccount666.serversystem.userdata.persistence;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Interface for handlers that can save and load field values to/from a configuration.
 * Implementations should handle specific data types.
 *
 * @param <T> The type of data this handler can process
 */
public interface FieldHandler<T> {
    /**
     * Saves a field value to the configuration.
     *
     * @param config The configuration to save to
     * @param path   The path in the configuration
     * @param value  The value to save
     */
    void save(FileConfiguration config, String path, T value);

    /**
     * Loads a field value from the configuration.
     *
     * @param config       The configuration to load from
     * @param path         The path in the configuration
     * @param defaultValue The default value to use if the path doesn't exist
     * @return The loaded value, or the default value if not found
     */
    T load(FileConfiguration config, String path, T defaultValue);
}