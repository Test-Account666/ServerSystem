package me.testaccount666.serversystem.managers.config;

import lombok.Getter;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.utils.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Default implementation of ConfigReader interface that handles loading, validation,
 * and operations on YAML configuration files.
 */
public class DefaultConfigReader implements ConfigReader {
    private final Plugin _plugin;
    @Getter
    private final File _file;
    @Getter
    private final FileConfiguration _configuration;
    private FileConfiguration _originalCfg = null;
    private DefaultConfigReader _newReader = null;

    /**
     * Creates a new DefaultConfigReader for the specified file and plugin.
     *
     * @param file   The configuration file to read
     * @param plugin The plugin associated with this configuration
     * @throws FileNotFoundException If the default configuration cannot be found
     */
    public DefaultConfigReader(File file, Plugin plugin) throws FileNotFoundException {
        _plugin = plugin;
        _file = file;
        _configuration = YamlConfiguration.loadConfiguration(file);
        loadDefaultConfig();

        if (!validateAndFixConfig()) createBackupAndSave();
    }

    /**
     * Static factory method to load a configuration file using the main plugin instance.
     *
     * @param file The configuration file to load
     * @return A ConfigReader instance for the file
     * @throws FileNotFoundException If the default configuration cannot be found
     */
    public static ConfigReader loadConfiguration(File file) throws FileNotFoundException {
        return loadConfiguration(file, ServerSystem.getPlugin(ServerSystem.class));
    }

    /**
     * Static factory method to load a configuration file with a specific plugin instance.
     *
     * @param file   The configuration file to load
     * @param plugin The plugin instance to use
     * @return A ConfigReader instance for the file
     * @throws FileNotFoundException If the default configuration cannot be found
     */
    public static ConfigReader loadConfiguration(File file, Plugin plugin) throws FileNotFoundException {
        return new DefaultConfigReader(file, plugin);
    }

    /**
     * Initializes a map of type warning messages for different config value types.
     *
     * @return Map of types to warning messages
     */
    private static Map<Class<?>, String> initializeTypeWarnings() {
        var typeWarnings = new HashMap<Class<?>, String>();
        typeWarnings.put(String.class, "should be a string");
        typeWarnings.put(Integer.class, "should be an integer");
        typeWarnings.put(Long.class, "should be a long");
        typeWarnings.put(Boolean.class, "should be a boolean");
        typeWarnings.put(Double.class, "should be a double");
        typeWarnings.put(ItemStack.class, "should be an ItemStack");
        return typeWarnings;
    }

    /**
     * Logs a configuration fix with a consistent format.
     *
     * @param key     The configuration key being fixed
     * @param problem The problem description
     */
    private void logConfigFix(String key, String problem) {
        ServerSystem.getLog().warning("Fixing ${problem} config entry '${key}' in file '${_file.getName()}'");
    }

    /**
     * Loads the default configuration from the plugin's resources.
     * For messages.yml, attempts to load a language-specific version.
     *
     * @throws FileNotFoundException If the default configuration file cannot be found
     */
    private void loadDefaultConfig() throws FileNotFoundException {
        var filename = _file.getName().toLowerCase();

        if (_plugin.getResource(filename) != null) {
            _originalCfg = YamlConfiguration.loadConfiguration(new InputStreamReader(_plugin.getResource(filename)));
            return;
        }

        if (filename.equalsIgnoreCase("messages.yml") || filename.equalsIgnoreCase("mappings.yml")) {
            var language = _configuration.getString("language", "english");
            var languageFile = "messages/${language}/${filename}";

            if (_plugin.getResource(languageFile) != null)
                _originalCfg = YamlConfiguration.loadConfiguration(new InputStreamReader(_plugin.getResource(languageFile)));
            else {
                _originalCfg = YamlConfiguration.loadConfiguration(new InputStreamReader(_plugin.getResource("messages/english/${filename}")));
                ServerSystem.getLog().warning("Couldn't find messages file for language '${language}', using English instead");
            }
            return;
        }

        throw new FileNotFoundException("Couldn't find default config file '${filename}'!");
    }

    /**
     * Validates the configuration against the default configuration.
     * Fixes missing entries and type mismatches.
     *
     * @return true if the configuration is valid (no fixes needed), false otherwise
     */
    protected boolean validateAndFixConfig() {
        if (_originalCfg == null) return true; // No default config to validate against

        var isValid = true;
        var typeWarnings = initializeTypeWarnings();
        var defaultSection = _originalCfg.getConfigurationSection("");

        if (defaultSection == null) return true; // Empty default config

        for (var key : defaultSection.getKeys(true)) {
            // Skip example entries
            if (key.toLowerCase(Locale.ROOT).contains("example")) continue;

            // Fix missing entries
            if (!_configuration.isSet(key)) {
                logConfigFix(key, "missing");
                _configuration.set(key, _originalCfg.get(key));
                isValid = false;
                continue;
            }

            // Fix type mismatches
            var userValue = _configuration.get(key);
            var defaultValue = _originalCfg.get(key);

            if (userValue == null || defaultValue == null) continue;

            var userType = userValue.getClass();
            var defaultType = defaultValue.getClass();

            if (userType.isAssignableFrom(defaultType)) continue;

            var warningMessage = typeWarnings.get(defaultType);
            if (warningMessage != null) {
                logConfigFix(key, warningMessage);
                _configuration.set(key, defaultValue);
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Creates a backup of the configuration file, saves changes, and reloads the configuration.
     */
    private void createBackupAndSave() {
        var filename = _file.getName();
        ServerSystem.getLog().warning("One or more errors with your '${filename}' file were found and fixed, a backup was made before saving");

        try {
            // Create a timestamped backup file
            var dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss");
            var now = LocalDateTime.now();
            var date = dtf.format(now);
            var backupFile = Path.of("plugins", "ServerSystem", "${filename}.backup-${date}").toFile();

            FileUtils.copyFile(_file, backupFile);

            save();
            reload();

        } catch (IOException exception) {
            exception.printStackTrace();
            ServerSystem.getLog().severe("An error occurred while backing up, changes only saved internally/temporarily!");
        }
    }

    @Override
    public Object getObject(String path, Object def) {
        var configReader = this;
        if (_newReader != null) configReader = _newReader;

        configReader.ensureConfigHasValue(path);
        return configReader._configuration.get(path, def);
    }

    @Override
    public Object getObject(String path) {
        return getObject(path, null);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        var configReader = this;
        if (_newReader != null) configReader = _newReader;

        configReader.ensureConfigHasValue(path);
        return configReader._configuration.getBoolean(path, def);
    }

    @Override
    public boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    @Override
    public String getString(String path, String def) {
        var configReader = this;
        if (_newReader != null) configReader = _newReader;

        configReader.ensureConfigHasValue(path);
        return configReader._configuration.getString(path, def);
    }

    @Override
    public String getString(String path) {
        return getString(path, null);
    }

    @Override
    public int getInt(String path, int def) {
        var configReader = this;
        if (_newReader != null) configReader = _newReader;

        configReader.ensureConfigHasValue(path);
        return configReader._configuration.getInt(path, def);
    }

    @Override
    public int getInt(String path) {
        return getInt(path, 0);
    }

    @Override
    public long getLong(String path, long def) {
        var configReader = this;
        if (_newReader != null) configReader = _newReader;

        configReader.ensureConfigHasValue(path);
        return configReader._configuration.getLong(path, def);
    }

    @Override
    public long getLong(String path) {
        return getLong(path, 0L);
    }

    @Override
    public double getDouble(String path, double def) {
        var configReader = this;
        if (_newReader != null) configReader = _newReader;

        configReader.ensureConfigHasValue(path);
        return configReader._configuration.getDouble(path, def);
    }

    @Override
    public double getDouble(String path) {
        return getDouble(path, 0.0D);
    }

    @Override
    public ItemStack getItemStack(String path, ItemStack def) {
        var configReader = this;
        if (_newReader != null) configReader = _newReader;

        configReader.ensureConfigHasValue(path);
        return configReader._configuration.getItemStack(path, def);
    }

    @Override
    public ItemStack getItemStack(String path) {
        return getItemStack(path, null);
    }

    @Override
    public void set(String path, Object object) {
        var configReader = this;
        if (_newReader != null) configReader = _newReader;

        configReader._configuration.set(path, object);
    }

    @Override
    public void save() {
        var configReader = this;
        if (_newReader != null) configReader = _newReader;

        try {
            configReader._configuration.save(configReader._file);
        } catch (IOException exception) {
            ServerSystem.getLog().severe("Failed to save configuration file '${configReader._file.getName()}'");
            exception.printStackTrace();
        }
    }

    @Override
    public void reload() {
        var configReader = this;
        if (_newReader != null) configReader = _newReader;

        try {
            configReader._configuration.load(configReader._file);
        } catch (IOException | InvalidConfigurationException exception) {
            ServerSystem.getLog().severe("Failed to reload configuration file '${configReader._file.getName()}'");
            exception.printStackTrace();
        }
    }

    @Override
    public void load(File file) {
        try {
            _newReader = new DefaultConfigReader(file, ServerSystem.Instance);
        } catch (Exception exception) {
            ServerSystem.getLog().severe("Failed to load configuration from file '${file.getName()}'");
            exception.printStackTrace();
        }
    }

    @Override
    public ConfigurationSection getConfigurationSection(String path) {
        var configReader = this;
        if (_newReader != null) configReader = _newReader;

        configReader.ensureConfigHasValue(path);
        return configReader._configuration.getConfigurationSection(path);
    }

    @Override
    public boolean isConfigurationSection(String path) {
        var configReader = this;
        if (_newReader != null) configReader = _newReader;

        return configReader._configuration.isConfigurationSection(path);
    }

    /**
     * Ensures a configuration value exists by restoring it from the default config if missing.
     *
     * @param path The configuration path to check
     */
    private void ensureConfigHasValue(String path) {
        if (_originalCfg == null) return;

        // Don't do anything if the value is already set or not in the default config
        if (_configuration.isSet(path) || !_originalCfg.isSet(path)) return;

        var partialPath = findClosestExistingParentPath(path);

        if (partialPath.isEmpty()) {
            // No parent exists, restore the entire section
            restoreConfigValue(path);
            return;
        }

        var section = _configuration.getConfigurationSection(partialPath);
        if (section == null) {
            restoreConfigValue(path);
            return;
        }

        section.set(path.substring(partialPath.length() + 1), _originalCfg.get(path));
        saveAndReload();
    }

    /**
     * Finds the closest existing parent path for the given path.
     *
     * @param path The configuration path to find a parent for
     * @return The closest existing parent path, or empty string if none found
     */
    private String findClosestExistingParentPath(String path) {
        if (!path.contains(".")) return "";

        var pathParts = path.split("\\.");
        var currentPath = new StringBuilder();

        for (var i = 0; i < pathParts.length - 1; i++) {
            if (i > 0) currentPath.append(".");
            currentPath.append(pathParts[i]);

            if (_configuration.isSet(currentPath.toString())) return currentPath.toString();
        }

        return "";
    }

    /**
     * Restores a configuration value from the default configuration.
     *
     * @param path The path to restore
     */
    private void restoreConfigValue(String path) {
        _configuration.set(path, _originalCfg.get(path));
        saveAndReload();
    }

    /**
     * Saves and reloads the configuration file.
     */
    private void saveAndReload() {
        try {
            _configuration.save(_file);
            _configuration.load(_file);
        } catch (IOException | InvalidConfigurationException exception) {
            ServerSystem.getLog().severe("Failed to save/reload configuration file '${_file.getName()}'");
            exception.printStackTrace();
        }
    }
}

