package me.testaccount666.serversystem.userdata.persistence;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * A marker class that indicates the system should automatically select
 * an appropriate handler based on the field type.
 * This class is not meant to be used directly.
 */
public final class DefaultFieldHandler implements FieldHandler<Object> {
    /**
     * Private constructor to prevent instantiation.
     * This class is only used as a marker in the SaveableField annotation.
     */
    private DefaultFieldHandler() {
        throw new UnsupportedOperationException("DefaultFieldHandler is a marker class and should not be instantiated");
    }

    @Override
    public void save(FileConfiguration config, String path, Object value) {
        throw new UnsupportedOperationException("DefaultFieldHandler is a marker class and should not be used directly");
    }

    @Override
    public Object load(FileConfiguration config, String path, Object defaultValue) {
        throw new UnsupportedOperationException("DefaultFieldHandler is a marker class and should not be used directly");
    }
}