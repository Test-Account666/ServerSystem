package me.testaccount666.serversystem;

import me.testaccount666.serversystem.commands.management.CommandManager;
import me.testaccount666.serversystem.managers.MessageManager;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.config.DefaultConfigReader;
import me.testaccount666.serversystem.managers.globaldata.MappingsData;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.nio.file.Path;

public final class ServerSystem extends JavaPlugin {
    public static ServerSystem Instance;
    private UserManager _userManager;
    private CommandManager _commandManager;

    @Override
    public void onEnable() {
        Instance = this;

        loadConfigsAndData();

        Bukkit.getScheduler().runTaskLater(this, _commandManager::registerCommands, 1);
    }

    private void loadConfigsAndData() {
        _userManager = new UserManager();

        try {
            var commandsConfigReader = loadCommandsConfig();
            _commandManager = new CommandManager(commandsConfigReader);
        } catch (FileNotFoundException exception) {
            throw new RuntimeException("Failed to load 'commands.yml'", exception);
        }

        var permissionsFile = Path.of("plugins", "ServerSystem", "permissions.yml").toFile();
        if (!permissionsFile.exists()) saveResource("permissions.yml", false);

        try {
            PermissionManager.initialize(this);
        } catch (FileNotFoundException exception) {
            throw new RuntimeException("Failed to load 'permissions.yml'", exception);
        }

        var messagesFile = Path.of("plugins", "ServerSystem", "messages.yml").toFile();
        if (!messagesFile.exists()) saveResource("messages.yml", false);

        try {
            MessageManager.initialize(this);
        } catch (FileNotFoundException exception) {
            throw new RuntimeException("Failed to load 'messages.yml'", exception);
        }

        var mappingsFile = Path.of("plugins", "ServerSystem", "mappings.yml").toFile();
        if (!mappingsFile.exists()) saveResource("mappings.yml", false);

        try {
            MappingsData.initialize(new DefaultConfigReader(mappingsFile, this));
        } catch (FileNotFoundException exception) {
            throw new RuntimeException("Failed to load 'mappings.yml'", exception);
        }
    }

    private ConfigReader loadCommandsConfig() throws FileNotFoundException {
        var commandsFile = Path.of("plugins", "ServerSystem", "commands.yml").toFile();

        if (!commandsFile.exists()) saveResource("commands.yml", false);

        return new DefaultConfigReader(commandsFile, this);
    }

    @Override
    public void onDisable() {
        if (_commandManager != null) _commandManager.unregisterCommands();
    }

    public UserManager getUserManager() {
        return _userManager;
    }
}
