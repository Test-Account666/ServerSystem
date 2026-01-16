package me.testaccount666.serversystem.userdata.persistence

import org.bukkit.configuration.file.FileConfiguration

/**
 * A marker class that indicates the system should automatically select
 * an appropriate handler based on the field type.
 * This class is not meant to be used directly.
 */
class DefaultFieldHandler private constructor() : FieldHandler<Any?> {
    /**
     * Private constructor to prevent instantiation.
     * This class is only used as a marker in the SaveableField annotation.
     */
    init {
        throw UnsupportedOperationException("DefaultFieldHandler is a marker class and should not be instantiated")
    }

    override fun save(config: FileConfiguration, path: String, value: Any?) {
        throw UnsupportedOperationException("DefaultFieldHandler is a marker class and should not be used directly")
    }

    override fun load(config: FileConfiguration, path: String, defaultValue: Any?): Any? {
        throw UnsupportedOperationException("DefaultFieldHandler is a marker class and should not be used directly")
    }
}