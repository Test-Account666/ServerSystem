package me.testaccount666.serversystem.extensions

import me.testaccount666.serversystem.managers.config.ConfigReader
import org.bukkit.configuration.ConfigurationSection

inline fun <reified T : Enum<T>> ConfigReader.getEnum(path: String, defaultValue: T): T {
    val value = getString(path) ?: return defaultValue
    return try {
        enumValueOf<T>(value.uppercase())
    } catch (e: IllegalArgumentException) {
        defaultValue
    }
}

inline fun <reified T : Enum<T>> ConfigurationSection.getEnum(path: String, defaultValue: T): T {
    val value = getString(path) ?: return defaultValue
    return try {
        enumValueOf<T>(value.uppercase())
    } catch (e: IllegalArgumentException) {
        defaultValue
    }
}
