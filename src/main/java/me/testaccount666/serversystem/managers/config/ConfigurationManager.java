package me.testaccount666.serversystem.managers.config;

import me.testaccount666.serversystem.managers.MessageManager;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.managers.globaldata.DefaultsData;
import me.testaccount666.serversystem.managers.globaldata.MappingsData;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

public class ConfigurationManager {
    private final Plugin _plugin;
    private final Path _pluginFolder;
    private ConfigReader _commandsConfig;
    private ConfigReader _economyConfig;
    private ConfigReader _userDataConfig;

    public ConfigurationManager(Plugin plugin) {
        _plugin = plugin;
        _pluginFolder = Path.of("plugins", "ServerSystem");
    }

    public void loadAllConfigs() throws FileNotFoundException {
        initializeCommandsConfig();
        initializePermissionsConfig();
        initializeMessagesConfig();
        initializeMappingsConfig();
        initializeDefaultsConfig();
        initializeEconomyConfig();
        initializeUserDataConfig();
        createUserDataFolder();
    }

    private void initializeUserDataConfig() throws FileNotFoundException {
        var userDataFile = _pluginFolder.resolve("userdata.yml").toFile();
        ensureConfigFileExists(userDataFile, "userdata.yml");
        _userDataConfig = new DefaultConfigReader(userDataFile, _plugin);
    }

    private void initializeCommandsConfig() throws FileNotFoundException {
        var commandsFile = _pluginFolder.resolve("commands.yml").toFile();
        ensureConfigFileExists(commandsFile, "commands.yml");
        _commandsConfig = new DefaultConfigReader(commandsFile, _plugin);
    }

    private void initializePermissionsConfig() throws FileNotFoundException {
        var permissionsFile = _pluginFolder.resolve("permissions.yml").toFile();
        ensureConfigFileExists(permissionsFile, "permissions.yml");
        PermissionManager.initialize(_plugin);
    }

    private void initializeMessagesConfig() throws FileNotFoundException {
        var messagesFile = _pluginFolder.resolve("messages.yml").toFile();
        ensureConfigFileExists(messagesFile, "messages.yml");
        MessageManager.initialize(_plugin);
    }

    private void initializeMappingsConfig() throws FileNotFoundException {
        var mappingsFile = _pluginFolder.resolve("mappings.yml").toFile();
        ensureConfigFileExists(mappingsFile, "mappings.yml");
        MappingsData.initialize(new DefaultConfigReader(mappingsFile, _plugin));
    }

    private void initializeDefaultsConfig() throws FileNotFoundException {
        var defaultsFile = _pluginFolder.resolve("defaults.yml").toFile();
        ensureConfigFileExists(defaultsFile, "defaults.yml");
        DefaultsData.initialize(new DefaultConfigReader(defaultsFile, _plugin));
    }

    private void initializeEconomyConfig() throws FileNotFoundException {
        var economyFile = _pluginFolder.resolve("economy.yml").toFile();
        ensureConfigFileExists(economyFile, "economy.yml");
        _economyConfig = new DefaultConfigReader(economyFile, _plugin);
    }

    private void createUserDataFolder() {
        var userDataFolder = _pluginFolder.resolve("UserData").toFile();
        if (!userDataFolder.exists()) userDataFolder.mkdirs();
    }

    private void ensureConfigFileExists(File configFile, String resourceName) {
        if (!configFile.exists()) _plugin.saveResource(resourceName, false);
    }

    public ConfigReader getCommandsConfig() {
        return _commandsConfig;
    }

    public ConfigReader getEconomyConfig() {
        return _economyConfig;
    }

    public ConfigReader getUserDataConfig() {
        return _userDataConfig;
    }
}
