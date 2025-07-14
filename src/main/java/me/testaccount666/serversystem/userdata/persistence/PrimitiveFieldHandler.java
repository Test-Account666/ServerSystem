package me.testaccount666.serversystem.userdata.persistence;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * A field handler for primitive types (boolean, int, long, double, String).
 * This handler uses the configuration's built-in methods for these types.
 *
 * @param <T> The primitive type
 */
public class PrimitiveFieldHandler<T> implements FieldHandler<T> {
    private final Class<T> _type;

    /**
     * Creates a new primitive field handler for the specified type.
     *
     * @param type The primitive type class
     */
    public PrimitiveFieldHandler(Class<T> type) {
        _type = type;
    }

    @Override
    public void save(FileConfiguration config, String path, T value) {
        config.set(path, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T load(FileConfiguration config, String path, T defaultValue) {
        if (!config.isSet(path)) return defaultValue;

        var value = config.get(path);

        //TODO: Do we need the isInstance check?

        // If the value is already of the correct type, return it
        if (_type.isInstance(value)) return (T) value;

        // Otherwise, try to convert it
        if (_type == Boolean.class || _type == boolean.class) return (T) Boolean.valueOf(config.getBoolean(path));
        if (_type == Integer.class || _type == int.class) return (T) Integer.valueOf(config.getInt(path));
        if (_type == Long.class || _type == long.class) return (T) Long.valueOf(config.getLong(path));
        if (_type == Double.class || _type == double.class) return (T) Double.valueOf(config.getDouble(path));
        if (_type == String.class) return (T) config.getString(path);

        // If we can't convert it, return the default value
        return defaultValue;
    }
}