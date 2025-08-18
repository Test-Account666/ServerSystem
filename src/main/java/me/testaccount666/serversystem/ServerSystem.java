package me.testaccount666.serversystem;

import lombok.Getter;
import lombok.SneakyThrows;
import me.testaccount666.migration.LegacyDataMigrator;
import me.testaccount666.migration.essentials.EssentialsMigrator;
import me.testaccount666.serversystem.clickablesigns.SignManager;
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager;
import me.testaccount666.serversystem.commands.executables.warp.manager.WarpManager;
import me.testaccount666.serversystem.commands.management.CommandManager;
import me.testaccount666.serversystem.commands.management.CommandReplacer;
import me.testaccount666.serversystem.listener.management.ListenerManager;
import me.testaccount666.serversystem.managers.config.ConfigurationManager;
import me.testaccount666.serversystem.managers.database.economy.AbstractEconomyDatabaseManager;
import me.testaccount666.serversystem.managers.database.economy.MySqlEconomyDatabaseManager;
import me.testaccount666.serversystem.managers.database.economy.SqliteEconomyDatabaseManager;
import me.testaccount666.serversystem.managers.database.moderation.AbstractModerationDatabaseManager;
import me.testaccount666.serversystem.managers.database.moderation.MySqlModerationDatabaseManager;
import me.testaccount666.serversystem.managers.database.moderation.SqliteModerationDatabaseManager;
import me.testaccount666.serversystem.placeholderapi.PlaceholderApiSupport;
import me.testaccount666.serversystem.placeholderapi.PlaceholderManager;
import me.testaccount666.serversystem.updates.UpdateManager;
import me.testaccount666.serversystem.userdata.CachedUser;
import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.UserManager;
import me.testaccount666.serversystem.userdata.money.EconomyProvider;
import me.testaccount666.serversystem.userdata.money.vault.EconomyVaultAPI;
import me.testaccount666.serversystem.utils.Version;
import me.testaccount666.serversystem.utils.VersionInfo;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ServerSystem extends JavaPlugin {
    public static final Version MINIMUM_MINECRAFT_VERSION = new Version("1.21");
    public static final Version CURRENT_VERSION = new Version(VersionInfo.CLEAN_VERSION);
    public static ServerSystem Instance;
    @Getter
    private static Logger _Log;
    @Getter
    private final ServiceRegistry _registry = new ServiceRegistry();

    public static Version getServerVersion() {
        var version = Bukkit.getMinecraftVersion();

        getLog().log(Level.FINE, "Server version: ${version}");

        return new Version(version);
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        Instance = this;
        _Log = getLogger();

        var minecraftVersion = getServerVersion();
        if (minecraftVersion.compareTo(MINIMUM_MINECRAFT_VERSION) < 0)
            getLog().warning("You're running an unsupported/legacy version of Minecraft! " +
                    "Please update to at least ${MINIMUM_MINECRAFT_VERSION.getVersion()}!");

        var migrator = new LegacyDataMigrator();
        if (migrator.isLegacyDataPresent()) {
            getLog().log(Level.INFO, "Legacy data detected. Attempting to migrate...");
            migrator.prepareMigration();
        }

        var previousVersionFile = new File(getDataFolder(), "previousVersion.yml");
        var previousVersionConfig = YamlConfiguration.loadConfiguration(previousVersionFile);
        previousVersionConfig.set("previousVersion", CURRENT_VERSION.toString());
        try {
            previousVersionConfig.save(previousVersionFile);
        } catch (IOException exception) {
            throw new RuntimeException("Error updating 'previousVersion'", exception);
        }

        getRegistry().registerService(EssentialsMigrator.class, new EssentialsMigrator());

        try {
            initialize();
        } catch (Exception exception) {
            getLog().log(Level.SEVERE, "Failed to initialize the plugin", exception);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        var configManager = getRegistry().getService(ConfigurationManager.class);
        getRegistry().registerService(UpdateManager.class, new UpdateManager(this, configManager)).start();

        Bukkit.getScheduler().runTaskLater(this, migrator::migrateLegacyData, 1L);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            var registry = getRegistry();

            registry.getServiceOptional(CommandManager.class).ifPresent(CommandManager::registerCommands);
            registry.getServiceOptional(ListenerManager.class).ifPresent(ListenerManager::registerListeners);
            registry.getServiceOptional(PlaceholderManager.class).ifPresent(PlaceholderManager::registerPlaceholders);
            registry.getServiceOptional(CommandReplacer.class).ifPresent(CommandReplacer::replaceCommands);

            //var essentialsMigrator = new EssentialsMigrator();
            //if (essentialsMigrator.isEssentialsInstalled()) essentialsMigrator.migrateTo();
        }, 2);
    }

    private void initialize() throws Exception {
        var registry = getRegistry();

        var configManager = new ConfigurationManager(this);
        registry.registerService(ConfigurationManager.class, configManager).loadAllConfigs();

        var moderationType = configManager.getModerationConfig().getString("Moderation.StorageType.Value");
        var moderationDbManager = switch (moderationType.toLowerCase()) {
            case "sqlite" -> new SqliteModerationDatabaseManager(getDataFolder());
            case "mysql" -> new MySqlModerationDatabaseManager(configManager.getModerationConfig());
            default -> throw new IllegalStateException("Unsupported moderation storage: ${moderationType} - Supported values: sqlite, mysql");
        };
        registry.registerService(AbstractModerationDatabaseManager.class, moderationDbManager);

        var essentialsMigrator = registry.getService(EssentialsMigrator.class);
        if (!essentialsMigrator.isEssentialsInstalled() && EconomyVaultAPI.isVaultInstalled()) EconomyVaultAPI.initialize();

        PlaceholderApiSupport.registerPlaceholders();

        moderationDbManager.initialize();

        var economyStorageType = configManager.getEconomyConfig().getString("Economy.StorageType.Value");
        var economyDbManager = switch (economyStorageType.toLowerCase()) {
            case "sqlite" -> new SqliteEconomyDatabaseManager(getDataFolder());
            case "mysql" -> new MySqlEconomyDatabaseManager(configManager.getEconomyConfig());
            default -> throw new IllegalStateException("Unsupported economy storage: ${economyStorageType} - Supported values: sqlite, mysql");
        };
        registry.registerService(AbstractEconomyDatabaseManager.class, economyDbManager);

        var commandManager = new CommandManager(configManager.getCommandsConfig());
        registry.registerService(CommandManager.class, commandManager);
        registry.registerService(ListenerManager.class, new ListenerManager(commandManager));
        registry.registerService(PlaceholderManager.class, new PlaceholderManager());
        registry.registerService(EconomyProvider.class, new EconomyProvider(configManager.getEconomyConfig()));
        registry.registerService(UserManager.class, new UserManager());

        registry.registerService(KitManager.class, new KitManager());

        registry.registerService(CommandReplacer.class, new CommandReplacer());

        Bukkit.getScheduler().runTask(this, () -> {
            var warpFile = Path.of(getDataFolder().getPath(), "data", "warps.yml").toFile();
            var warpConfig = YamlConfiguration.loadConfiguration(warpFile);

            registry.registerService(WarpManager.class, new WarpManager(warpConfig, warpFile));
            registry.registerService(SignManager.class, new SignManager()).loadSignTypes();
        });
    }

    @Override
    public void onDisable() {
        var registry = getRegistry();

        registry.getServiceOptional(UserManager.class).ifPresent(this::saveAllUsers);

        registry.getServiceOptional(CommandManager.class).ifPresent(CommandManager::unregisterCommands);
        registry.getServiceOptional(ListenerManager.class).ifPresent(ListenerManager::unregisterListeners);

        PlaceholderApiSupport.unregisterPlaceholders();

        registry.getServiceOptional(AbstractEconomyDatabaseManager.class).ifPresent(AbstractEconomyDatabaseManager::shutdown);
        registry.getServiceOptional(AbstractModerationDatabaseManager.class).ifPresent(AbstractModerationDatabaseManager::shutdown);

        registry.clearServices();
    }

    private void saveAllUsers(UserManager userManager) {
        userManager.getCachedUsers().stream().map(CachedUser::getOfflineUser).forEach(OfflineUser::save);
    }
}
