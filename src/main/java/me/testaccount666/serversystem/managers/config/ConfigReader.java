package me.testaccount666.serversystem.managers.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public interface ConfigReader {

    FileConfiguration getConfiguration();

    File getFile();

    Object getObject(String path, Object def);

    default Object getObject(String path) {
        return getObject(path, null);
    }

    boolean getBoolean(String path, boolean def);

    default boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    String getString(String path, String def);

    default String getString(String path) {
        return getString(path, null);
    }

    int getInt(String path, int def);

    default int getInt(String path) {
        return getInt(path, 0);
    }

    long getLong(String path, long def);

    default long getLong(String path) {
        return getLong(path, 0);
    }

    double getDouble(String path, double def);

    default double getDouble(String path) {
        return getDouble(path, 0);
    }

    ItemStack getItemStack(String path, ItemStack def);

    ItemStack getItemStack(String path);
    default ItemStack getItemStack(String path) {
        return getItemStack(path, null);
    }

    void set(String path, Object object);

    void save();

    void reload();

    void load(File file);

    ConfigurationSection getConfigurationSection(String path);

    boolean isConfigurationSection(String path);
}

