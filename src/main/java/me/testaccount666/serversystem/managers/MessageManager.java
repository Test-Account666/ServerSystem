package me.testaccount666.serversystem.managers;

import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.config.DefaultConfigReader;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Optional;

//TODO: Completely forgot, this needs user-specific language support!
public class MessageManager {
    private static final File _MESSAGES_FILE = Path.of("plugins", "ServerSystem", "messages.yml").toFile();
    private static ConfigReader _ConfigReader;
    private static PlaceholderManager _PlaceholderManager;

    private MessageManager() {
    }

    public static void initialize(Plugin plugin) throws FileNotFoundException {
        _ConfigReader = new DefaultConfigReader(_MESSAGES_FILE, plugin);
        _PlaceholderManager = new PlaceholderManager();
    }

    public static Optional<String> getCommandMessage(CommandSender commandSender, String messagePath, @Nullable String targetName, String label) {
        return getCommandMessage(commandSender, messagePath, targetName, label, true);
    }

    public static Optional<String> getCommandMessage(CommandSender commandSender, String messagePath, @Nullable String targetName, String label, boolean addPrefix) {
        return getFormattedMessage(commandSender, "Commands.${messagePath}", targetName, label, addPrefix);
    }

    public static Optional<String> getCommandMessage(User user, String messagePath, @Nullable String targetName, String label) {
        return getCommandMessage(user.getCommandSender(), messagePath, targetName, label);
    }

    public static Optional<String> getCommandMessage(User user, String messagePath, @Nullable String targetName, String label, boolean addPrefix) {
        return getCommandMessage(user.getCommandSender(), messagePath, targetName, label, addPrefix);
    }

    public static Optional<String> getNoPermissionMessage(CommandSender commandSender, String permission, @Nullable String targetName, String label) {
        return getNoPermissionMessage(commandSender, permission, targetName, label, true);
    }

    public static Optional<String> getNoPermissionMessage(CommandSender commandSender, String permission, @Nullable String targetName, String label, boolean addPrefix) {
        return getFormattedMessage(commandSender, "General.NoPermission", targetName, label, addPrefix)
                .map(message -> message.replace("<PERMISSION>", PermissionManager.getPermission(permission)));
    }

    public static Optional<String> getNoPermissionMessage(User user, String permission, @Nullable String targetName, String label) {
        return getNoPermissionMessage(user.getCommandSender(), permission, targetName, label);
    }

    public static Optional<String> getNoPermissionMessage(User user, String permission, @Nullable String targetName, String label, boolean addPrefix) {
        return getNoPermissionMessage(user.getCommandSender(), permission, targetName, label, addPrefix);
    }

    public static Optional<String> getFormattedMessage(CommandSender commandSender, String messagePath) {
        return getFormattedMessage(commandSender, messagePath, null, null);
    }

    public static Optional<String> getFormattedMessage(CommandSender commandSender, String messagePath, boolean addPrefix) {
        return getFormattedMessage(commandSender, messagePath, null, null, addPrefix);
    }

    public static Optional<String> getFormattedMessage(User user, String messagePath) {
        return getFormattedMessage(user.getCommandSender(), messagePath);
    }

    public static Optional<String> getFormattedMessage(User user, String messagePath, boolean addPrefix) {
        return getFormattedMessage(user.getCommandSender(), messagePath, addPrefix);
    }

    public static Optional<String> getFormattedMessage(CommandSender commandSender, String messagePath, @Nullable String targetName, @Nullable String label) {
        return getFormattedMessage(commandSender, messagePath, targetName, label, true);
    }

    public static Optional<String> getFormattedMessage(CommandSender commandSender, String messagePath, @Nullable String targetName, @Nullable String label, boolean addPrefix) {
        return getMessage(messagePath).map(message -> formatMessage(message, commandSender, targetName, label, addPrefix));
    }

    public static Optional<String> getFormattedMessage(User user, String messagePath, @Nullable String targetName, @Nullable String label) {
        return getFormattedMessage(user.getCommandSender(), messagePath, targetName, label);
    }

    public static Optional<String> getFormattedMessage(User user, String messagePath, @Nullable String targetName, @Nullable String label, boolean addPrefix) {
        return getFormattedMessage(user.getCommandSender(), messagePath, targetName, label, addPrefix);
    }

    public static String formatMessage(String message, User user) {
        return formatMessage(message, user.getCommandSender());
    }

    public static String formatMessage(String message, User user, boolean addPrefix) {
        return formatMessage(message, user.getCommandSender(), addPrefix);
    }

    public static String formatMessage(String message, CommandSender commandSender) {
        return formatMessage(message, commandSender, null, null);
    }

    public static String formatMessage(String message, CommandSender commandSender, boolean addPrefix) {
        return formatMessage(message, commandSender, null, null, addPrefix);
    }

    public static String formatMessage(String message, User user, @Nullable String targetName, @Nullable String label) {
        return formatMessage(message, user.getCommandSender(), targetName, label);
    }

    public static String formatMessage(String message, User user, @Nullable String targetName, @Nullable String label, boolean addPrefix) {
        return formatMessage(message, user.getCommandSender(), targetName, label, addPrefix);
    }

    public static String formatMessage(String message, CommandSender commandSender, @Nullable String targetName, @Nullable String label) {
        return formatMessage(message, commandSender, targetName, label, true);
    }

    public static String formatMessage(String message, CommandSender commandSender, @Nullable String targetName, @Nullable String label, boolean addPrefix) {
        if (message == null) return "";

        if (addPrefix) {
            var prefix = getMessage("General.Prefix").orElse("");
            message = prefix + message;
        }

        return ChatColor.translateColorCodes(_PlaceholderManager.applyPlaceholders(message, commandSender, targetName, label != null? label : ""));
    }

    public static Optional<String> getMessage(String messagePath) {
        if (_ConfigReader == null) throw new IllegalStateException("MessageManager was not yet initialized. Call initialize first.");

        messagePath = "Messages.${messagePath}";

        var message = _ConfigReader.getString(messagePath, null);

        if (message == null) {
            Bukkit.getLogger().warning("Message '${messagePath}' not found!");
            return Optional.empty();
        }

        return Optional.of(message);
    }
}
