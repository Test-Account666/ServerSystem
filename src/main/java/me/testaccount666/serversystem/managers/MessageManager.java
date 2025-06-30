package me.testaccount666.serversystem.managers;

import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.config.DefaultConfigReader;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Optional;

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

    public static boolean sendCommandMessage(CommandSender commandSender, String messagePath) {
        return sendMessage(commandSender, "Commands.${messagePath}");
    }

    public static boolean sendCommandMessage(User user, String messagePath) {
        return sendMessage(user, "Commands.${messagePath}");
    }

    public static boolean sendMessage(CommandSender commandSender, String messagePath) {
        return sendMessage(commandSender, messagePath, null, null);
    }

    public static boolean sendMessage(User user, String messagePath) {
        return sendMessage(user.getCommandSender(), messagePath, null, null);
    }

    public static boolean sendMessage(CommandSender commandSender, String messagePath, @Nullable String targetName, @Nullable String label) {
        var messageOptional = getMessage(messagePath);

        return messageOptional.map(message -> sendMessageString(commandSender, message, targetName, label))
                .orElse(false);
    }

    public static boolean sendMessage(User user, String messagePath, @Nullable String targetName, @Nullable String label) {
        return sendMessage(user.getCommandSender(), messagePath, targetName, label);
    }

    public static boolean sendMessageString(CommandSender commandSender, String message) {
        return sendMessageString(commandSender, message, null, null);
    }

    public static boolean sendMessageString(User user, String message) {
        return sendMessageString(user.getCommandSender(), message, null, null);
    }

    public static boolean sendMessageString(CommandSender commandSender, String message, @Nullable String targetName, @Nullable String label) {
        if (message == null) return false;

        message = _PlaceholderManager.applyPlaceholders(message, commandSender, targetName, label != null ? label : "");
        commandSender.sendMessage(message);

        return true;
    }

    public static boolean sendMessageString(User user, String message, @Nullable String targetName, @Nullable String label) {
        return sendMessageString(user.getCommandSender(), message, targetName, label);
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
