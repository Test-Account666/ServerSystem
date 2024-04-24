package me.entity303.serversystem.utils;

import me.entity303.serversystem.config.IConfigReader;
import me.entity303.serversystem.config.DefaultConfigReader;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.CommandSender;

import java.io.File;

public class PermissionsChecker {
    private final ServerSystem _plugin;
    private final IConfigReader _configuration;

    public PermissionsChecker(ServerSystem plugin) {
        this._plugin = plugin;

        var permissionsFile = new File("plugins//ServerSystem", "permissions.yml");
        this._configuration = DefaultConfigReader.LoadConfiguration(permissionsFile, this._plugin);
    }

    public boolean HasPermission(CommandSender commandSender, String action, boolean disableNoPermissionMessage) {
        var permission = this._configuration.GetString("Permissions." + action);
        if (ServerSystem.DEBUG)
            this._plugin.Info("Permission used: " + permission + "!");
        if (permission == null) {
            this._plugin.Error("Error in Permission: " + action);
            this._plugin.Warn("(denying permission)");
            return false;
        }
        if (disableNoPermissionMessage)
            return commandSender.hasPermission(permission);
        else
            return this.HasPermission(commandSender, action);
    }

    public boolean HasPermission(CommandSender commandSender, String action) {
        var permission = this._configuration.GetString("Permissions." + action);
        if (ServerSystem.DEBUG)
            this._plugin.Info("Permission used: " + permission + "!");
        if (permission == null) {
            this._plugin.Info(ChatColor.TranslateAlternateColorCodes('&', this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.NoPermissionInfo"))
                                      .replace("<SENDER>", commandSender.getName()));
            this._plugin.Error("Error in Permission: " + action);
            this._plugin.Warn("(denying permission)");
            return false;
        }
        if (!commandSender.hasPermission(permission)) {
            this._plugin.Info(ChatColor.TranslateAlternateColorCodes('&', this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.NoPermissionInfo"))
                                      .replace("<SENDER>", commandSender.getName()));
            return false;
        }
        return true;
    }

    public boolean HasPermissionString(CommandSender commandSender, String permission, boolean noFuck) {
        if (ServerSystem.DEBUG)
            this._plugin.Info("Permission used: " + permission + "!");
        if (noFuck)
            return commandSender.hasPermission(permission);
        else
            return this.HasPermissionString(commandSender, permission);
    }

    public boolean HasPermissionString(CommandSender commandSender, String permission) {
        if (ServerSystem.DEBUG)
            this._plugin.Info("Permission used: " + permission + "!");
        if (!commandSender.hasPermission(permission)) {
            this._plugin.Info(ChatColor.TranslateAlternateColorCodes('&', this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.NoPermissionInfo"))
                                      .replace("<SENDER>", commandSender.getName()));
            return false;
        }
        return true;
    }

    public String GetPermission(String action) {
        return this._configuration.GetString("Permissions." + action);
    }

    public IConfigReader GetConfiguration() {
        return this._configuration;
    }
}
