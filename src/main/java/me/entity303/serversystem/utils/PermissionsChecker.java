package me.entity303.serversystem.utils;

import me.entity303.serversystem.config.ConfigReader;
import me.entity303.serversystem.config.DefaultConfigReader;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.CommandSender;

import java.io.File;

public class PermissionsChecker {
    private final ServerSystem plugin;
    private final ConfigReader configuration;

    public PermissionsChecker(ServerSystem plugin) {
        this.plugin = plugin;

        var permissionsFile = new File("plugins//ServerSystem", "permissions.yml");
        this.configuration = DefaultConfigReader.loadConfiguration(permissionsFile, this.plugin);
    }

    public boolean hasPermission(CommandSender cs, String action, boolean disableNoPermissionMessage) {
        var permission = this.configuration.getString("Permissions." + action);
        if (ServerSystem.debug)
            this.plugin.log("Permission used: " + permission + "!");
        if (permission == null) {
            this.plugin.error("Error in Permission: " + action);
            this.plugin.warn("(denying permission)");
            return false;
        }
        if (disableNoPermissionMessage)
            return cs.hasPermission(permission);
        else
            return this.hasPermission(cs, action);
    }

    public boolean hasPermission(CommandSender commandSender, String action) {
        var permission = this.configuration.getString("Permissions." + action);
        if (ServerSystem.debug)
            this.plugin.log("Permission used: " + permission + "!");
        if (permission == null) {
            this.plugin.log(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.NoPermissionInfo"))
                                     .replace("<SENDER>", commandSender.getName()));
            this.plugin.error("Error in Permission: " + action);
            this.plugin.warn("(denying permission)");
            return false;
        }
        if (!commandSender.hasPermission(permission)) {
            this.plugin.log(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.NoPermissionInfo"))
                                     .replace("<SENDER>", commandSender.getName()));
            return false;
        }
        return true;
    }

    public boolean hasPermissionString(CommandSender cs, String permission, boolean noFuck) {
        if (ServerSystem.debug)
            this.plugin.log("Permission used: " + permission + "!");
        if (noFuck)
            return cs.hasPermission(permission);
        else
            return this.hasPermissionString(cs, permission);
    }

    public boolean hasPermissionString(CommandSender cs, String permission) {
        if (ServerSystem.debug)
            this.plugin.log("Permission used: " + permission + "!");
        if (!cs.hasPermission(permission)) {
            this.plugin.log(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.NoPermissionInfo"))
                                     .replace("<SENDER>", cs.getName()));
            return false;
        }
        return true;
    }

    public String getPermission(String action) {
        return this.configuration.getString("Permissions." + action);
    }

    public ConfigReader getConfiguration() {
        return this.configuration;
    }
}
