package me.testaccount666.serversystem.userdata.persistence

import org.bukkit.configuration.file.FileConfiguration
import java.util.*
import java.util.stream.Collectors

/**
 * A field handler for sets of UUIDs.
 * This handler saves UUIDs as strings and loads them back as UUIDs.
 */
class UuidSetFieldHandler : FieldHandler<Set<UUID>> {
    override fun save(config: FileConfiguration, path: String, value: Set<UUID>?) {
        if (value.isNullOrEmpty()) {
            config.set(path, null)
            return
        }

        val uuidStrings = value.stream().map { it.toString() }.collect(Collectors.toList())

        config.set(path, uuidStrings)
    }

    override fun load(config: FileConfiguration, path: String, defaultValue: Set<UUID>?): Set<UUID>? {
        if (!config.isSet(path)) return defaultValue ?: HashSet()

        val uuidStrings = config.getStringList(path)
        if (uuidStrings.isEmpty()) return defaultValue ?: HashSet()

        return uuidStrings.stream().map<UUID> { name -> UUID.fromString(name) }.collect(Collectors.toSet())
    }
}