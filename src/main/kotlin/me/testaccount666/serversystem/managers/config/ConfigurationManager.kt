package me.testaccount666.serversystem.managers.config

import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.managers.globaldata.DefaultsData
import me.testaccount666.serversystem.managers.messages.MappingsData
import me.testaccount666.serversystem.managers.messages.MessageManager
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path

class ConfigurationManager(private val _plugin: Plugin) {
    private val _pluginFolder: Path = Path.of("plugins", "ServerSystem")

    lateinit var generalConfig: ConfigReader

    lateinit var commandsConfig: ConfigReader

    lateinit var economyConfig: ConfigReader

    lateinit var moderationConfig: ConfigReader

    lateinit var commandReplaceConfig: ConfigReader

    @Throws(FileNotFoundException::class)
    fun loadAllConfigs() {
        initializeGeneralConfig()
        initializeCommandsConfig()
        initializePermissionsConfig()
        initializeMappingsData()
        initializeDefaultsConfig()
        initializeMessagesConfig()
        initializeEconomyConfig()
        initializeModerationConfig()
        initializeCommandReplaceConfig()
        createUserDataFolder()
    }

    private fun initializeMappingsData() {
        MappingsData()
    }

    @Throws(FileNotFoundException::class)
    private fun initializeGeneralConfig() {
        _plugin.saveDefaultConfig()
        val generalFile = _pluginFolder.resolve("config.yml").toFile()
        generalConfig = DefaultConfigReader(generalFile, _plugin)
    }

    @Throws(FileNotFoundException::class)
    private fun initializeCommandsConfig() {
        val commandsFile = _pluginFolder.resolve("commands.yml").toFile()
        ensureConfigFileExists(commandsFile, "commands.yml")
        commandsConfig = DefaultConfigReader(commandsFile, _plugin)
    }

    @Throws(FileNotFoundException::class)
    private fun initializePermissionsConfig() {
        val permissionsFile = _pluginFolder.resolve("permissions.yml").toFile()
        ensureConfigFileExists(permissionsFile, "permissions.yml")
        PermissionManager.initialize(_plugin)
    }

    @Throws(FileNotFoundException::class)
    private fun initializeMessagesConfig() {
        MessageManager.initialize()
    }

    @Throws(FileNotFoundException::class)
    private fun initializeDefaultsConfig() {
        val defaultsFile = _pluginFolder.resolve("defaults.yml").toFile()
        ensureConfigFileExists(defaultsFile, "defaults.yml")
        DefaultsData.initialize(DefaultConfigReader(defaultsFile, _plugin))
    }

    @Throws(FileNotFoundException::class)
    private fun initializeEconomyConfig() {
        val economyFile = _pluginFolder.resolve("economy.yml").toFile()
        ensureConfigFileExists(economyFile, "economy.yml")
        economyConfig = DefaultConfigReader(economyFile, _plugin)
    }

    @Throws(FileNotFoundException::class)
    private fun initializeModerationConfig() {
        val moderationFile = _pluginFolder.resolve("moderation.yml").toFile()
        ensureConfigFileExists(moderationFile, "moderation.yml")
        moderationConfig = DefaultConfigReader(moderationFile, _plugin)
    }

    @Throws(FileNotFoundException::class)
    private fun initializeCommandReplaceConfig() {
        val replacedCommandsFile = _pluginFolder.resolve("replacedCommands.yml").toFile()
        ensureConfigFileExists(replacedCommandsFile, "replacedCommands.yml")
        commandReplaceConfig = NonValidatingConfigReader(replacedCommandsFile, _plugin)
    }

    private fun createUserDataFolder() {
        val userDataFolder = _pluginFolder.resolve("UserData").toFile()
        if (!userDataFolder.exists()) userDataFolder.mkdirs()
    }

    private fun ensureConfigFileExists(configFile: File, resourceName: String) {
        if (!configFile.exists()) _plugin.saveResource(resourceName, false)
    }
}