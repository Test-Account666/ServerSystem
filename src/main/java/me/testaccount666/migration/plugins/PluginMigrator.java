package me.testaccount666.migration.plugins;

import org.bukkit.plugin.Plugin;

public interface PluginMigrator {
    void migrateFrom();

    void migrateTo();

    Plugin getPlugin();
}
