package me.testaccount666.serversystem.userdata.persistence

import org.bukkit.configuration.file.FileConfiguration

/**
 * A field handler for primitive types (boolean, int, long, double, String).
 * This handler uses the configuration's built-in methods for these types.
 *
 * @param <T> The primitive type
</T> */
class PrimitiveFieldHandler<T>(private val _type: Class<T>) : FieldHandler<T> {
    override fun save(config: FileConfiguration, path: String, value: T?) {
        config.set(path, value)
    }

    override fun load(config: FileConfiguration, path: String, defaultValue: T?): T? {
        if (!config.isSet(path)) return defaultValue

        val value = config.get(path)

        //TODO: Do we need the isInstance check?

        // If the value is already of the correct type, return it
        if (_type.isInstance(value)) return value as T

        // Otherwise, try to convert it
        if (_type == Boolean::class.java || _type == Boolean::class.javaPrimitiveType) return config.getBoolean(path) as T?
        if (_type == Int::class.java || _type == Int::class.javaPrimitiveType) return config.getInt(path) as T?
        if (_type == Long::class.java || _type == Long::class.javaPrimitiveType) return config.getLong(path) as T?
        if (_type == Double::class.java || _type == Double::class.javaPrimitiveType) return config.getDouble(path) as T?
        if (_type == String::class.java) return config.getString(path) as T?

        // If we can't convert it, return the default value
        return defaultValue
    }
}