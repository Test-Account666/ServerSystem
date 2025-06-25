package me.testaccount666.serversystem.managers;

import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.config.DefaultConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

public class PermissionManager {
    private static final File PERMISSION_FILE = Path.of("plugins", "ServerSystem", "permissions.yml").toFile();
    private static ConfigReader ConfigReader;

    public static void initialize(Plugin plugin) throws FileNotFoundException {
        ConfigReader = new DefaultConfigReader(PERMISSION_FILE, plugin);
    }

    public static boolean hasCommandPermission(CommandSender commandSender, String permissionPath) {
        return hasPermission(commandSender, "Commands.${permissionPath}");
    }

    public static boolean hasPermission(CommandSender commandSender, String permissionPath) {
        return hasPermission(commandSender, permissionPath, true);
    }

    public static boolean hasPermission(CommandSender commandSender, String permissionPath, boolean sendFailInfo) {
        var permission = getPermission(permissionPath);

        return hasPermissionString(commandSender, permission, sendFailInfo);
    }

    public static boolean hasPermissionString(CommandSender commandSender, String permission) {
        return hasPermissionString(commandSender, permission, true);
    }

    public static boolean hasPermissionString(CommandSender commandSender, String permission, boolean sendFailInfo) {
        if (permission == null) return false;
        var hasPermission = commandSender.hasPermission(permission);

        if (!hasPermission && sendFailInfo)
            Bukkit.getLogger().info("${commandSender.getName()} has failed a permission check! Permission: ${permission}");

        return hasPermission;
    }

    public static String getPermission(String permissionPath) {
        if (ConfigReader == null) throw new IllegalStateException("PermissionManager was not yet initialized. Call initialize first.");

        permissionPath = "Permissions.${permissionPath}";

        var permission = ConfigReader.getString(permissionPath, null);

        if (permission == null) Bukkit.getLogger().warning("Permission '${permissionPath}' not found! (Denying permission)");

        return permission;
    }
}
