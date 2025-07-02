package me.testaccount666.serversystem.managers;

import me.testaccount666.serversystem.managers.globaldata.MappingsData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class PlaceholderManager {
    private boolean _placeholders;

    public PlaceholderManager() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            _placeholders = true;
        } catch (Throwable throwable) {
            _placeholders = false;
        }
    }

    /**
     * Replaces placeholders in the given message with dynamic values based on the provided
     * {@code commandSender}, {@code targetName}, and {@code label}. The method also integrates
     * with the PlaceholderAPI if enabled and the {@code commandSender} is a player.
     *
     * @param message       The message containing placeholders to be replaced.
     * @param commandSender The sender of the command whose name can replace placeholders such as {@code <SENDER>}.
     * @param targetName    The name of the target to replace the {@code <TARGET>} placeholder. If null, it defaults to the sender's name.
     * @param label         The command label to replace the {@code <LABEL>} placeholder.
     * @return The message with placeholders replaced by the corresponding values.
     */
    public String applyPlaceholders(String message, CommandSender commandSender, @Nullable String targetName, String label) {
        if (targetName == null) targetName = commandSender.getName();

        message = applyColorPlaceholder(message, "<Color:Prefix>", "Prefix");
        message = applyColorPlaceholder(message, "<Color:Separators>", "Separator");
        message = applyColorPlaceholder(message, "<Color:Message>", "Message");
        message = applyColorPlaceholder(message, "<Color:Highlight>", "Highlight");
        message = applyColorPlaceholder(message, "<Color:Error.Message>", "ErrorMessage");
        message = applyColorPlaceholder(message, "<Color:Error.Highlight>", "ErrorHighlight");

        message = message.replace("<SENDER>", commandSender.getName())
                .replace("<TARGET>", targetName)
                .replace("<LABEL>", label);

        if (_placeholders && commandSender instanceof Player player) message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);

        return message;
    }

    private String applyColorPlaceholder(String message, String placeholder, String colorId) {
        var colorOptional = MappingsData.MessageColors().getMessageColor(colorId);

        return colorOptional.map(prefixString -> message.replace(placeholder, prefixString))
                .orElseGet(() -> {
                    Bukkit.getLogger().warning("${colorId} color could not be found! This should not happen!");
                    return message;
                });
    }
}
