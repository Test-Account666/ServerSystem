package me.testaccount666.serversystem;

import lombok.Getter;
import lombok.Setter;
import me.testaccount666.migration.LegacyDataMigrator;
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager;
import me.testaccount666.serversystem.commands.executables.warp.manager.WarpManager;
import me.testaccount666.serversystem.commands.management.CommandManager;
import me.testaccount666.serversystem.listener.management.ListenerManager;
import me.testaccount666.serversystem.managers.config.ConfigurationManager;
import me.testaccount666.serversystem.managers.database.economy.AbstractEconomyDatabaseManager;
import me.testaccount666.serversystem.managers.database.economy.MySqlEconomyDatabaseManager;
import me.testaccount666.serversystem.managers.database.economy.SqliteEconomyDatabaseManager;
import me.testaccount666.serversystem.managers.database.moderation.AbstractModerationDatabaseManager;
import me.testaccount666.serversystem.managers.database.moderation.MySqlModerationDatabaseManager;
import me.testaccount666.serversystem.managers.database.moderation.SqliteModerationDatabaseManager;
import me.testaccount666.serversystem.userdata.CachedUser;
import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.UserManager;
import me.testaccount666.serversystem.userdata.money.EconomyProvider;
import me.testaccount666.serversystem.userdata.money.vault.VaultAPI;
import me.testaccount666.serversystem.utils.Version;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;

public final class ServerSystem extends JavaPlugin {
    private final static int _CURRENT_VERSION = 300;
    public static ServerSystem Instance;
    @Getter
    private UserManager _userManager;
    @Getter
    private CommandManager _commandManager;
    @Getter
    private ListenerManager _listenerManager;
    @Getter
    private EconomyProvider _economyProvider;
    @Getter
    private ConfigurationManager _configManager;
    @Getter
    private AbstractEconomyDatabaseManager _economyDatabaseManager;
    @Getter
    private WarpManager _warpManager;
    @Getter
    private AbstractModerationDatabaseManager _moderationDatabaseManager;
    @Getter
    @Setter
    @Nullable
    private KitManager _kitManager;

    public static Version getServerVersion() {
        var version = Bukkit.getVersion();

        version = version.substring(version.indexOf("MC: ") + 4, version.indexOf(")"));

        Bukkit.getLogger().log(Level.FINE, "Server version: ${version}");

        return new Version(version);
    }

    @Override
    public void onEnable() {
        Instance = this;

        var migrator = new LegacyDataMigrator();
        if (migrator.isLegacyDataPresent()) {
            Bukkit.getLogger().log(Level.INFO, "Legacy data detected. Attempting to migrate...");
            migrator.prepareMigration();
        }

        var previousVersionFile = new File(getDataFolder(), "previousVersion.yml");
        var previousVersionConfig = YamlConfiguration.loadConfiguration(previousVersionFile);
        previousVersionConfig.set("previousVersion", _CURRENT_VERSION);
        try {
            previousVersionConfig.save(previousVersionFile);
        } catch (IOException exception) {
            throw new RuntimeException("Error updating 'previousVersion'", exception);
        }

        try {
            initialize();
        } catch (Exception e) {
            getLogger().severe("Failed to initialize the plugin: ${e.getMessage()}");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        migrator.migrateLegacyData();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            _commandManager.registerCommands();
            _listenerManager.registerListeners();
        }, 1);
    }

    private void initialize() throws Exception {
        _configManager = new ConfigurationManager(this);
        _configManager.loadAllConfigs();

        var moderationType = _configManager.getModerationConfig().getString("Moderation.StorageType.Value");
        _moderationDatabaseManager = switch (moderationType.toLowerCase()) {
            case "sqlite" -> new SqliteModerationDatabaseManager(getDataFolder());
            case "mysql" -> new MySqlModerationDatabaseManager(_configManager.getModerationConfig());
            default -> throw new IllegalStateException("Unsupported moderation storage: ${moderationType} - Supported values: sqlite, mysql");
        };

        var warpFile = Path.of(getDataFolder().getPath(), "data", "warps.yml").toFile();
        var warpConfig = YamlConfiguration.loadConfiguration(warpFile);
        _warpManager = new WarpManager(warpConfig, warpFile);

        _moderationDatabaseManager.initialize();

        var economyStorageType = _configManager.getEconomyConfig().getString("Economy.StorageType.Value");
        _economyDatabaseManager = switch (economyStorageType.toLowerCase()) {
            case "sqlite" -> new SqliteEconomyDatabaseManager(getDataFolder());
            case "mysql" -> new MySqlEconomyDatabaseManager(_configManager.getEconomyConfig());
            default -> throw new IllegalStateException("Unsupported economy storage: ${economyStorageType} - Supported values: sqlite, mysql");
        };

        _commandManager = new CommandManager(_configManager.getCommandsConfig());
        _listenerManager = new ListenerManager(_commandManager);
        _economyProvider = new EconomyProvider(_configManager.getEconomyConfig());
        _userManager = new UserManager();

        if (VaultAPI.isVaultInstalled()) VaultAPI.initialize();
    }

    @Override
    public void onDisable() {
        if (_userManager != null) saveAllUsers();

        if (_commandManager != null) _commandManager.unregisterCommands();

        if (_listenerManager != null) _listenerManager.unregisterListeners();

        if (_economyDatabaseManager != null) _economyDatabaseManager.shutdown();

        if (_moderationDatabaseManager != null) _moderationDatabaseManager.shutdown();
    }

    private void saveAllUsers() {
        _userManager.getCachedUsers().stream().map(CachedUser::getOfflineUser).forEach(OfflineUser::save);
    }
}
