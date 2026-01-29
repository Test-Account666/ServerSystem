package me.testaccount666.serversystem.userdata.persistence

import org.bukkit.configuration.file.FileConfiguration
import java.util.*

/**
 * A field handler for sets of UUIDs.
 * This handler saves UUIDs as strings and loads them back as UUIDs.
 */
class UuidSetFieldHandler : FieldHandler<HashSet<UUID>> {
    override fun save(config: FileConfiguration, path: String, value: HashSet<UUID>?) {
        if (value.isNullOrEmpty()) {
            config.set(path, null)
            return
        }

        config.set(path, value.map { it.toString() })
    }

    override fun load(config: FileConfiguration, path: String, defaultValue: HashSet<UUID>?): HashSet<UUID> {
        if (!config.isSet(path)) return defaultValue ?: HashSet()

        val uuidStrings = config.getStringList(path)
        if (uuidStrings.isEmpty()) return defaultValue ?: HashSet()

        return uuidStrings.map { UUID.fromString(it) }.toHashSet()
    }
}