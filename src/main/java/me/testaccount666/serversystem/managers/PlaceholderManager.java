package me.testaccount666.serversystem.managers;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.messages.MappingsData;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;

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
    public String applyPlaceholders(String message, User commandSender, @Nullable String targetName, String label) {
        if (commandSender.getName().isEmpty()) {
            ServerSystem.getLog().warning("CommandSender (${commandSender.getUuid()}) has no name! This should not happen!");
            return message;
        }

        if (targetName == null) targetName = commandSender.getName().get();

        message = applyColorPlaceholder(commandSender, message, "<Color:Prefix>", "Prefix");
        message = applyColorPlaceholder(commandSender, message, "<Color:Separators>", "Separator");
        message = applyColorPlaceholder(commandSender, message, "<Color:Message>", "Message");
        message = applyColorPlaceholder(commandSender, message, "<Color:Highlight>", "Highlight");
        message = applyColorPlaceholder(commandSender, message, "<Color:Error.Message>", "ErrorMessage");
        message = applyColorPlaceholder(commandSender, message, "<Color:Error.Highlight>", "ErrorHighlight");

        message = message.replace("<SENDER>", commandSender.getName().get())
                .replace("<TARGET>", targetName)
                .replace("<LABEL>", label);

        if (_placeholders && !(commandSender instanceof ConsoleUser))
            message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(commandSender.getPlayer(), message);

        return message;
    }

    private String applyColorPlaceholder(User user, String message, String placeholder, String colorId) {
        var colorOptional = MappingsData.messageColors(user).getMessageColor(colorId);

        return colorOptional.map(prefixString -> message.replace(placeholder, prefixString))
                .orElseGet(() -> {
                    ServerSystem.getLog().warning("${colorId} color could not be found! This should not happen!");
                    return message;
                });
    }
}
