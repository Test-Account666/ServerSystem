package me.testaccount666.serversystem.managers.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public interface ConfigReader {

    FileConfiguration getConfiguration();

    File getFile();

    Object getObject(String path, Object def);

    Object getObject(String path);

    boolean getBoolean(String path, boolean def);

    boolean getBoolean(String path);

    String getString(String path, String def);

    String getString(String path);

    int getInt(String path, int def);

    int getInt(String path);

    long getLong(String path, long def);

    long getLong(String path);

    double getDouble(String path, double def);

    double getDouble(String path);

    ItemStack getItemStack(String path, ItemStack def);

    ItemStack getItemStack(String path);

    void set(String path, Object object);

    void save();

    void reload();

    void load(File file);

    ConfigurationSection getConfigurationSection(String path);

    boolean isConfigurationSection(String path);
}

