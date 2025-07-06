package me.testaccount666.serversystem.managers;

import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.config.DefaultConfigReader;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

public class PermissionManager {
    private static final File _PERMISSION_FILE = Path.of("plugins", "ServerSystem", "permissions.yml").toFile();
    private static ConfigReader _ConfigReader;

    public static void initialize(Plugin plugin) throws FileNotFoundException {
        _ConfigReader = new DefaultConfigReader(_PERMISSION_FILE, plugin);
    }

    public static boolean hasCommandPermission(CommandSender commandSender, String permissionPath) {
        return hasPermission(commandSender, "Commands.${permissionPath}");
    }

    public static boolean hasCommandPermission(User commandSender, String permissionPath) {
        return hasCommandPermission(commandSender.getCommandSender(), permissionPath);
    }

    public static boolean hasPermission(CommandSender commandSender, String permissionPath) {
        return hasPermission(commandSender, permissionPath, true);
    }

    public static boolean hasPermission(CommandSender commandSender, String permissionPath, boolean sendFailInfo) {
        if (!isPermissionRequired(permissionPath)) return true;

        var permission = getPermission("${permissionPath}");

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

    private static boolean isPermissionRequired(String permissionPath) {
        if (_ConfigReader == null) throw new IllegalStateException("PermissionManager was not yet initialized. Call initialize first.");

        permissionPath = "Permissions.${permissionPath}.Required";

        return _ConfigReader.getBoolean(permissionPath, true);
    }

    public static String getPermission(String permissionPath) {
        if (_ConfigReader == null) throw new IllegalStateException("PermissionManager was not yet initialized. Call initialize first.");

        permissionPath = "Permissions.${permissionPath}.Value";

        var permission = _ConfigReader.getString(permissionPath, null);

        if (permission == null) Bukkit.getLogger().warning("Permission '${permissionPath}' not found! (Denying permission)");

        return permission;
    }
}
