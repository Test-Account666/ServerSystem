package me.testaccount666.serversystem.managers;

import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.config.DefaultConfigReader;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import org.bukkit.Bukkit;
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

    public static String formatMessage(String message, User commandSender, @Nullable String targetName, @Nullable String label, boolean addPrefix) {
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
