package me.testaccount666.serversystem;

import lombok.Getter;
import me.testaccount666.serversystem.commands.executables.warp.manager.WarpManager;
import me.testaccount666.serversystem.commands.management.CommandManager;
import me.testaccount666.serversystem.listener.management.ListenerManager;
import me.testaccount666.serversystem.managers.config.ConfigurationManager;
import me.testaccount666.serversystem.managers.database.EconomyDatabaseManager;
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

import java.nio.file.Path;
import java.util.logging.Level;

public final class ServerSystem extends JavaPlugin {
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
    private EconomyDatabaseManager _economyDatabaseManager;
    @Getter
    private WarpManager _warpManager;
    @Getter
    private AbstractModerationDatabaseManager _moderationDatabaseManager;

    public static Version getServerVersion() {
        var version = Bukkit.getVersion();

        version = version.substring(version.indexOf("MC: ") + 4, version.indexOf(")"));

        Bukkit.getLogger().log(Level.FINE, "Server version: ${version}");

        return new Version(version);
    }

    @Override
    public void onEnable() {
        Instance = this;

        try {
            initialize();
        } catch (Exception e) {
            getLogger().severe("Failed to initialize the plugin: ${e.getMessage()}");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

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

        _economyDatabaseManager = new EconomyDatabaseManager(_configManager.getEconomyConfig());
        _commandManager = new CommandManager(_configManager.getCommandsConfig());
        _listenerManager = new ListenerManager(_commandManager);
        _economyProvider = new EconomyProvider(_configManager.getEconomyConfig(), _economyDatabaseManager);
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
