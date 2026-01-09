package me.testaccount666.serversystem

import me.testaccount666.migration.LegacyDataMigrator
import me.testaccount666.migration.plugins.MigratorRegistry
import me.testaccount666.serversystem.clickablesigns.SignManager
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager
import me.testaccount666.serversystem.commands.executables.warp.manager.WarpManager
import me.testaccount666.serversystem.commands.management.CommandManager
import me.testaccount666.serversystem.commands.management.CommandReplacer
import me.testaccount666.serversystem.listener.management.ListenerManager
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.managers.database.economy.AbstractEconomyDatabaseManager
import me.testaccount666.serversystem.managers.database.economy.MySqlEconomyDatabaseManager
import me.testaccount666.serversystem.managers.database.economy.SqliteEconomyDatabaseManager
import me.testaccount666.serversystem.managers.database.moderation.AbstractModerationDatabaseManager
import me.testaccount666.serversystem.managers.database.moderation.MySqlModerationDatabaseManager
import me.testaccount666.serversystem.managers.database.moderation.SqliteModerationDatabaseManager
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
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class ServerSystem : JavaPlugin() {
    val registry = ServiceRegistry()

    override fun onEnable() {
        instance = this
        log = logger

        val minecraftVersion: Version = serverVersion
        if (minecraftVersion < MINIMUM_MINECRAFT_VERSION) log.warning(
            "You're running an unsupported/legacy version of Minecraft! " +
                    "Please update to at least ${MINIMUM_MINECRAFT_VERSION.version}!"
        )

        val migrator = LegacyDataMigrator()
        if (migrator.isLegacyDataPresent()) {
            log.log(Level.INFO, "Legacy data detected. Attempting to migrate...")
            migrator.prepareMigration()
        }

        val previousVersionFile = File(dataFolder, "previousVersion.yml")
        val previousVersionConfig = YamlConfiguration.loadConfiguration(previousVersionFile)
        previousVersionConfig.set("previousVersion", CURRENT_VERSION.toString())
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

        Bukkit.getScheduler().runTaskLater(this, Runnable { migrator.migrateLegacyData() }, 1L)

        Bukkit.getScheduler().runTaskLater(this, Runnable {
            registry.getServiceOptional<CommandManager>().ifPresent(CommandManager::registerCommands)
            registry.getServiceOptional<ListenerManager>().ifPresent(ListenerManager::registerListeners)
            registry.getServiceOptional<PlaceholderManager>().ifPresent(PlaceholderManager::registerPlaceholders)
            registry.getServiceOptional<CommandReplacer>().ifPresent(CommandReplacer::replaceCommands)
        }, 2)
    }

    private fun initialize() {
        val registry = registry

        val configManager = ConfigurationManager(this)
        registry.registerService(configManager).loadAllConfigs()

        val moderationType = configManager.moderationConfig!!.getString("Moderation.StorageType.Value")
        requireNotNull(moderationType) { "Moderation storage type not set?!" }

        val moderationDbManager = when (moderationType.lowercase(Locale.getDefault())) {
            "sqlite" -> SqliteModerationDatabaseManager(dataFolder)
            "mysql" -> MySqlModerationDatabaseManager(configManager.moderationConfig)
            else -> error("Unsupported moderation storage: $moderationType - Supported values: sqlite, mysql")
        }
        registry.registerService<AbstractModerationDatabaseManager>(moderationDbManager)

        val migratorRegistry = MigratorRegistry()
        registry.registerService(migratorRegistry).registerMigrators()

        PlaceholderApiSupport.registerPlaceholders()

        moderationDbManager.initialize()

        val economyStorageType = configManager.economyConfig!!.getString("Economy.StorageType.Value")
        requireNotNull(economyStorageType) { "Economy storage type not set?!" }

        val economyDbManager = when (economyStorageType.lowercase(Locale.getDefault())) {
            "sqlite" -> SqliteEconomyDatabaseManager(dataFolder)
            "mysql" -> MySqlEconomyDatabaseManager(configManager.economyConfig)
            else -> error("Unsupported economy storage: $economyStorageType - Supported values: sqlite, mysql")
        }
        registry.registerService<AbstractEconomyDatabaseManager>(economyDbManager)

        val commandManager = CommandManager(configManager.commandsConfig)
        registry.registerService(commandManager)
        registry.registerService(ListenerManager(commandManager))
        registry.registerService(PlaceholderManager())
        registry.registerService(EconomyProvider(configManager.economyConfig!!))
        registry.registerService(UserManager())

        registry.registerService(KitManager())

        registry.registerService(CommandReplacer())


        val migratorCount = migratorRegistry.migrators.size

        if (migratorCount == 0 && configManager.economyConfig!!.getBoolean("Economy.HookIntoVault")) {
            if (EconomyVaultAPI.isVaultInstalled) EconomyVaultAPI.initialize()
        }


        if (migratorCount > 0) log.info("Not hooking into Vault since we found data migrators!")

        Bukkit.getScheduler().runTask(this, Runnable {
            val warpFile = Path.of(dataFolder.path, "data", "warps.yml").toFile()
            val warpConfig = YamlConfiguration.loadConfiguration(warpFile)

            registry.registerService(WarpManager(warpConfig, warpFile))
            registry.registerService(SignManager()).loadSignTypes()
        })
    }

    override fun onDisable() {
        registry.getServiceOptional<UserManager>().ifPresent(this::saveAllUsers)

        registry.getServiceOptional<CommandManager>().ifPresent(CommandManager::unregisterCommands)
        registry.getServiceOptional<ListenerManager>().ifPresent(ListenerManager::unregisterListeners)

        PlaceholderApiSupport.unregisterPlaceholders()

        registry.getServiceOptional<AbstractEconomyDatabaseManager>().ifPresent(AbstractEconomyDatabaseManager::shutdown)
        registry.getServiceOptional<AbstractModerationDatabaseManager>().ifPresent(AbstractModerationDatabaseManager::shutdown)

        registry.clearServices()
    }

    private fun saveAllUsers(userManager: UserManager) = userManager.cachedUsers.forEach { it.offlineUser.save() }

    companion object {
        val MINIMUM_MINECRAFT_VERSION: Version = Version("1.21")

        @JvmField
        val CURRENT_VERSION: Version = Version(VersionInfo.CLEAN_VERSION)

        @JvmStatic
        lateinit var instance: ServerSystem

        @JvmStatic
        lateinit var log: Logger

        val serverVersion: Version by lazy {
            val version = Bukkit.getMinecraftVersion()

            log.log(Level.FINE, "Server version: $version")

            return@lazy Version(version)
        }
    }
}
