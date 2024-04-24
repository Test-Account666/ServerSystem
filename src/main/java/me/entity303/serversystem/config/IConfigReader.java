package me.entity303.serversystem.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public interface IConfigReader {

    FileConfiguration GetConfiguration();

    File GetFile();

    Object GetObject(String path, Object def);

    Object GetObject(String path);

    boolean GetBoolean(String path, boolean def);

    boolean GetBoolean(String path);

    String GetString(String path, String def);

    String GetString(String path);

    int GetInt(String path, int def);

    int GetInt(String path);

    long GetLong(String path, long def);

    long GetLong(String path);

    double GetDouble(String path, double def);

    double GetDouble(String path);

    ItemStack GetItemStack(String path, ItemStack def);

    ItemStack GetItemStack(String path);

    void Set(String path, Object object);

    void Save();

    void Reload();

    void Load(File file);

    ConfigurationSection GetConfigurationSection(String path);

    boolean IsConfigurationSection(String path);
}
