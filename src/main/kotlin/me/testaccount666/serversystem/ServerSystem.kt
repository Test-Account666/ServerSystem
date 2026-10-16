package me.testaccount666.serversystem

import me.testaccount666.migration.LegacyDataMigrator
import me.testaccount666.migration.plugins.MigratorRegistry
import me.testaccount666.paperktx.PaperKTX
import me.testaccount666.paperktx.scheduler.skedule.okkero.schedule
import me.testaccount666.serversystem.clickablesigns.SignManager
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager
import me.testaccount666.serversystem.commands.executables.waypoints.warp.manager.WarpManager
import me.testaccount666.serversystem.commands.management.CommandManager
import me.testaccount666.serversystem.commands.management.CommandReplacer
import me.testaccount666.serversystem.listener.management.ListenerManager
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.managers.database.economy.*
import me.testaccount666.serversystem.managers.database.moderation.*
import me.testaccount666.serversystem.placeholderapi.PlaceholderApiSupport
import me.testaccount666.serversystem.placeholderapi.PlaceholderManager
import me.testaccount666.serversystem.updates.UpdateManager
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.userdata.money.EconomyProvider
import me.testaccount666.serversystem.userdata.money.vault.EconomyVaultAPI
import me.testaccount666.serversystem.utils.Version
import me.testaccount666.serversystem.utils.VersionInfo
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.logging.Level
import java.util.logging.Logger

class ServerSystem : JavaPlugin() {
    val registry = ServiceRegistry()

    override fun onLoad() {
        instance = this
        log = logger

        if (serverVersion < MINIMUM_MINECRAFT_VERSION) {
            log.warning(
                "You're running an unsupported/legacy version of Minecraft! " +
                        "Please update to at least ${MINIMUM_MINECRAFT_VERSION.version}!"
            )
        }
    }

    override fun onEnable() {
        PaperKTX.plugin = this
        val migrator = LegacyDataMigrator()
        if (migrator.isLegacyDataPresent) {
            log.log(Level.INFO, "Legacy data detected. Attempting to migrate...")
            migrator.prepareMigration()
        }

        val previousVersionFile = File(dataFolder, "previousVersion.yml")
        val previousVersionConfig = YamlConfiguration.loadConfiguration(previousVersionFile)
        previousVersionConfig["previousVersion"] = CURRENT_VERSION.toString()
        try {
            previousVersionConfig.save(previousVersionFile)
        } catch (exception: IOException) {
            throw RuntimeException("Error updating 'previousVersion'", exception)
        }

        try {
            initialize()
        } catch (exception: Exception) {
            log.log(Level.SEVERE, "Failed to initialize the plugin", exception)
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        val configManager = registry.getService<ConfigurationManager>()
        registry.registerService(UpdateManager(this, configManager)).start()

        schedule {
            waitFor(1)
            migrator.migrateLegacyData()
        }

        schedule {
            waitFor(2)
            registry.getServiceOrNull<CommandManager>()?.registerCommands()
            registry.getServiceOrNull<ListenerManager>()?.registerListeners()
            registry.getServiceOrNull<PlaceholderManager>()?.registerPlaceholders()
            registry.getServiceOrNull<CommandReplacer>()?.replaceCommands()
        }
    }

    private fun initialize() {
        val configManager = ConfigurationManager(this)
        registry.registerService(configManager).loadAllConfigs()

        val moderationType = configManager.moderationConfig.getString("Moderation.StorageType.Value")
        requireNotNull(moderationType) { "Moderation storage type not set?!" }

        val moderationDbManager =
            when (moderationType.lowercase()) {
                "sqlite" -> SqliteModerationDatabaseManager(dataFolder)
                "mysql" -> MySqlModerationDatabaseManager(configManager.moderationConfig)
                else -> error("Unsupported moderation storage: $moderationType - Supported values: sqlite, mysql")
            }
        registry.registerService<ModerationDatabaseManager>(moderationDbManager)

        val migratorRegistry = registry.registerService(MigratorRegistry()).also(MigratorRegistry::registerMigrators)
        PlaceholderApiSupport.registerPlaceholders()

        moderationDbManager.initialize()

        val economyStorageType = configManager.economyConfig.getString("Economy.StorageType.Value")
        requireNotNull(economyStorageType) { "Economy storage type not set?!" }

        val economyDbManager =
            when (economyStorageType.lowercase()) {
                "sqlite" -> SqliteEconomyDatabaseManager(dataFolder)
                "mysql" -> MySqlEconomyDatabaseManager(configManager.economyConfig)
                else -> error("Unsupported economy storage: $economyStorageType - Supported values: sqlite, mysql")
            }
        registry.registerService<EconomyDatabaseManager>(economyDbManager)

        val commandManager = CommandManager(configManager.commandsConfig)
        registry.registerService(commandManager)
        registry.registerService(ListenerManager(commandManager))
        registry.registerService(PlaceholderManager())
        registry.registerService(EconomyProvider(configManager.economyConfig))
        registry.registerService(UserManager())

        registry.registerService(KitManager())

        registry.registerService(CommandReplacer())

        val migratorCount = migratorRegistry.migrators.size

        if (migratorCount == 0 && configManager.economyConfig.getBoolean("Economy.HookIntoVault")) {
            if (EconomyVaultAPI.isVaultInstalled) EconomyVaultAPI.initialize()
        }

        if (migratorCount > 0) log.info("Not hooking into Vault since we found data migrators!")

        schedule {
            val warpFile = Path.of(dataFolder.path, "data", "warps.yml").toFile()
            val warpConfig = YamlConfiguration.loadConfiguration(warpFile)

            registry.registerService(WarpManager(warpConfig, warpFile))
            registry.registerService(SignManager()).loadSignTypes()
        }
    }

    override fun onDisable() = with(registry) {
        getServiceOrNull<UserManager>()?.run(::saveAllUsers)

        getServiceOrNull<CommandManager>()?.run(CommandManager::unregisterCommands)
        getServiceOrNull<ListenerManager>()?.run(ListenerManager::unregisterListeners)

        PlaceholderApiSupport.unregisterPlaceholders()

        getServiceOrNull<EconomyDatabaseManager>()?.shutdown()
        getServiceOrNull<ModerationDatabaseManager>()?.shutdown()

        clearServices()
    }

    private fun saveAllUsers(userManager: UserManager) = userManager.cachedUsers.forEach { it.offlineUser.save() }

    companion object {
        val MINIMUM_MINECRAFT_VERSION = Version("1.21")

        @JvmField
        val CURRENT_VERSION = Version(VersionInfo.CLEAN_VERSION)

        @JvmStatic
        lateinit var instance: ServerSystem

        @JvmStatic
        lateinit var log: Logger

        val serverVersion by lazy {
            val version = Bukkit.getMinecraftVersion()

            log.log(Level.FINE, "Server version: $version")

            return@lazy Version(version)
        }
    }
}
