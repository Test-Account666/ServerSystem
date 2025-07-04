package me.testaccount666.serversystem;

import me.testaccount666.serversystem.commands.management.CommandManager;
import me.testaccount666.serversystem.listener.management.ListenerManager;
import me.testaccount666.serversystem.managers.config.ConfigurationManager;
import me.testaccount666.serversystem.managers.database.DatabaseManager;
import me.testaccount666.serversystem.userdata.CachedUser;
import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.UserManager;
import me.testaccount666.serversystem.userdata.money.EconomyManager;
import me.testaccount666.serversystem.userdata.money.vault.VaultAPI;
import me.testaccount666.serversystem.utils.Version;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class ServerSystem extends JavaPlugin {
    public static ServerSystem Instance;
    private UserManager _userManager;
    private CommandManager _commandManager;
    private ListenerManager _listenerManager;
    private EconomyManager _economyManager;
    private ConfigurationManager _configManager;
    private DatabaseManager _databaseManager;

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
            getLogger().severe("Failed to initialize the plugin: " + e.getMessage());
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

        _databaseManager = new DatabaseManager(_configManager.getEconomyConfig());
        _commandManager = new CommandManager(_configManager.getCommandsConfig());
        _listenerManager = new ListenerManager(_commandManager);
        _economyManager = new EconomyManager(_configManager.getEconomyConfig(), _databaseManager);
        _userManager = new UserManager();

        if (VaultAPI.isVaultInstalled()) VaultAPI.initialize();
    }

    @Override
    public void onDisable() {
        if (_userManager != null) saveAllUsers();

        if (_commandManager != null) _commandManager.unregisterCommands();

        if (_listenerManager != null) _listenerManager.unregisterListeners();

        if (_databaseManager != null) _databaseManager.shutdown();
    }

    private void saveAllUsers() {
        _userManager.getCachedUsers().stream().map(CachedUser::getOfflineUser).forEach(OfflineUser::save);
    }

    /**
     * Gets the UserManager instance for this plugin.
     *
     * @return The UserManager instance that manages all user-related functionality
     */
    public UserManager getUserManager() {
        return _userManager;
    }

    /**
     * Gets the EconomyManager instance for this plugin.
     *
     * @return The EconomyManager instance that manages all economy-related functionality (Mostly config)
     */
    public EconomyManager getEconomyManager() {
        return _economyManager;
    }

    /**
     * Gets the DatabaseManager instance for this plugin.
     *
     * @return The DatabaseManager instance that manages database connections
     */
    public DatabaseManager getDatabaseManager() {
        return _databaseManager;
    }

    /**
     * Gets the ConfigurationManager instance for this plugin.
     *
     * @return The ConfigurationManager instance that manages all configurations
     */
    public ConfigurationManager getConfigManager() {
        return _configManager;
    }
}
