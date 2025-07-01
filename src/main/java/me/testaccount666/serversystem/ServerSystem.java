package me.testaccount666.serversystem;

import me.testaccount666.serversystem.commands.management.CommandManager;
import me.testaccount666.serversystem.listener.management.ListenerManager;
import me.testaccount666.serversystem.managers.config.ConfigurationManager;
import me.testaccount666.serversystem.userdata.CachedUser;
import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerSystem extends JavaPlugin {
    public static ServerSystem Instance;
    private UserManager _userManager;
    private CommandManager _commandManager;
    private ListenerManager _listenerManager;
    private ConfigurationManager _configManager;

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

        _listenerManager.registerListeners();
        Bukkit.getScheduler().runTaskLater(this, _commandManager::registerCommands, 1);
    }

    private void initialize() throws Exception {
        _configManager = new ConfigurationManager(this);
        _configManager.loadAllConfigs();

        _userManager = new UserManager();
        _commandManager = new CommandManager(_configManager.getCommandsConfig());
        _listenerManager = new ListenerManager();
    }

    @Override
    public void onDisable() {
        if (_userManager != null) saveAllUsers();

        if (_commandManager != null) _commandManager.unregisterCommands();

        if (_listenerManager != null) _listenerManager.unregisterListeners();
    }

    private void saveAllUsers() {
        _userManager.getCachedUsers().stream().map(CachedUser::getOfflineUser).forEach(OfflineUser::save);
    }

    public UserManager getUserManager() {
        return _userManager;
    }
}