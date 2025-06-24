package me.testaccount666.serversystem;

import me.testaccount666.serversystem.commands.management.CommandManager;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerSystem extends JavaPlugin {
    public static ServerSystem Instance;
    private UserManager userManager;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        Instance = this;

        userManager = new UserManager();
        commandManager = new CommandManager();

        Bukkit.getScheduler().runTaskLater(this, commandManager::registerCommands, 1);
    }

    @Override
    public void onDisable() {

    }

    public UserManager getUserManager() {
        return userManager;
    }
}
