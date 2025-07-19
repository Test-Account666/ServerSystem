package me.testaccount666.serversystem.managers.config;

import lombok.Getter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.managers.globaldata.DefaultsData;
import me.testaccount666.serversystem.managers.messages.MappingsData;
import me.testaccount666.serversystem.managers.messages.MessageManager;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

public class ConfigurationManager {
    private final Plugin _plugin;
    private final Path _pluginFolder;
    @Getter
    ConfigReader _generalConfig;
    @Getter
    private ConfigReader _commandsConfig;
    @Getter
    private ConfigReader _economyConfig;
    @Getter
    private ConfigReader _moderationConfig;

    public ConfigurationManager(Plugin plugin) {
        _plugin = plugin;
        _pluginFolder = Path.of("plugins", "ServerSystem");
    }

    public void loadAllConfigs() throws FileNotFoundException {
        initializeGeneralConfig();
        initializeCommandsConfig();
        initializePermissionsConfig();
        initializeMappingsData();
        initializeDefaultsConfig();
        initializeMessagesConfig();
        initializeEconomyConfig();
        initializeModerationConfig();
        createUserDataFolder();
    }

    private void initializeMappingsData() {
        new MappingsData();
    }

    private void initializeGeneralConfig() throws FileNotFoundException {
        _plugin.saveDefaultConfig();
        var generalFile = _pluginFolder.resolve("config.yml").toFile();
        _generalConfig = new DefaultConfigReader(generalFile, _plugin);
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
        MessageManager.initialize();
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

    private void initializeModerationConfig() throws FileNotFoundException {
        var moderationFile = _pluginFolder.resolve("moderation.yml").toFile();
        ensureConfigFileExists(moderationFile, "moderation.yml");
        _moderationConfig = new DefaultConfigReader(moderationFile, _plugin);
    }

    private void createUserDataFolder() {
        var userDataFolder = _pluginFolder.resolve("UserData").toFile();
        if (!userDataFolder.exists()) userDataFolder.mkdirs();
    }

    private void ensureConfigFileExists(File configFile, String resourceName) {
        if (!configFile.exists()) _plugin.saveResource(resourceName, false);
    }

}
