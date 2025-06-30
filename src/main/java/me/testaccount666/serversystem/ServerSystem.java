package me.testaccount666.serversystem;

import me.testaccount666.serversystem.commands.management.CommandManager;
import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.config.DefaultConfigReader;
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

        ConfigReader commandsConfigReader;

        try {
            commandsConfigReader = loadCommandsConfig();
        } catch (FileNotFoundException exception) {
            throw new RuntimeException("Failed to load 'commands.yml'!", exception);
        }

        _userManager = new UserManager();
        _commandManager = new CommandManager(commandsConfigReader);

        Bukkit.getScheduler().runTaskLater(this, _commandManager::registerCommands, 1);
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
