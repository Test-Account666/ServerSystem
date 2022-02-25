package me.entity303.serversystem.utils;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.config.ConfigReader;
import me.entity303.serversystem.config.DefaultConfigReader;
import org.bukkit.command.CommandSender;

import java.io.File;

public class permissions {
    private final ServerSystem plugin;
    private final File permFile;
    private final ConfigReader cfg;

    public permissions(ServerSystem plugin) {
        this.plugin = plugin;
        this.permFile = new File("plugins//ServerSystem", "permissions.yml");
        this.cfg = DefaultConfigReader.loadConfiguration(this.permFile);
    }

    public boolean hasPerm(CommandSender cs, String action) {
        String permission = this.cfg.getString("Permissions." + action);
        if (ServerSystem.debug) this.plugin.log("Permission used: " + permission + "!");
        if (permission == null) {
            this.plugin.log(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.NoPermissionInfo")).replace("<SENDER>", cs.getName()));
            this.plugin.error("Error in Permission: " + action);
            this.plugin.warn("(denying permission)");
            return false;
        }
        if (!cs.hasPermission(permission)) {
            this.plugin.log(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.NoPermissionInfo")).replace("<SENDER>", cs.getName()));
            return false;
        }
        return true;
    }

    public boolean hasPerm(CommandSender cs, String action, boolean noFuck) {
        String permission = this.cfg.getString("Permissions." + action);
        if (ServerSystem.debug) this.plugin.log("Permission used: " + permission + "!");
        if (permission == null) {
            this.plugin.error("Error in Permission: " + action);
            this.plugin.warn("(denying permission)");
            return false;
        }
        if (noFuck)
            return cs.hasPermission(permission);
        else
            return this.hasPerm(cs, action);
    }

    public boolean hasPermString(CommandSender cs, String permission) {
        if (ServerSystem.debug) this.plugin.log("Permission used: " + permission + "!");
        if (!cs.hasPermission(permission)) {
            this.plugin.log(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.NoPermissionInfo")).replace("<SENDER>", cs.getName()));
            return false;
        }
        return true;
    }

    public boolean hasPermString(CommandSender cs, String permission, boolean noFuck) {
        if (ServerSystem.debug) this.plugin.log("Permission used: " + permission + "!");
        if (noFuck)
            return cs.hasPermission(permission);
        else
            return this.hasPermString(cs, permission);
    }

    public String Perm(String action) {
        return this.cfg.getString("Permissions." + action);
    }

    public ConfigReader getCfg() {
        return this.cfg;
    }
}
