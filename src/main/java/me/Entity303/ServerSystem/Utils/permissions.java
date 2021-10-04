package me.Entity303.ServerSystem.Utils;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class permissions {
    private final ss plugin;
    private final File permFile;
    private final FileConfiguration cfg;

    public permissions(ss plugin) {
        this.plugin = plugin;
        this.permFile = new File("plugins//ServerSystem", "permissions.yml");
        this.cfg = YamlConfiguration.loadConfiguration(this.permFile);
    }

    public boolean hasPerm(CommandSender cs, String action) {
        String permission = this.cfg.getString("Permissions." + action);
        if (ss.debug) this.plugin.log("Permission used: " + permission + "!");
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
        if (ss.debug) this.plugin.log("Permission used: " + permission + "!");
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
        if (ss.debug) this.plugin.log("Permission used: " + permission + "!");
        if (!cs.hasPermission(permission)) {
            this.plugin.log(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.NoPermissionInfo")).replace("<SENDER>", cs.getName()));
            return false;
        }
        return true;
    }

    public boolean hasPermString(CommandSender cs, String permission, boolean noFuck) {
        if (ss.debug) this.plugin.log("Permission used: " + permission + "!");
        if (noFuck)
            return cs.hasPermission(permission);
        else
            return this.hasPermString(cs, permission);
    }

    public String Perm(String action) {
        return this.cfg.getString("Permissions." + action);
    }

    public FileConfiguration getCfg() {
        return this.cfg;
    }
}
