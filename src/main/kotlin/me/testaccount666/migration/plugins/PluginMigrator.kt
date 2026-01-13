package me.testaccount666.migration.plugins

import org.bukkit.plugin.Plugin

interface PluginMigrator {
    fun migrateFrom()

    fun migrateTo()

    val plugin: Plugin
}
