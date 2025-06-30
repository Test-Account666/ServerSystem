package me.testaccount666.serversystem.managers;

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

    public String applyPlaceholders(String message, CommandSender commandSender, @Nullable String targetName, String label) {
        if (targetName == null) targetName = commandSender.getName();

        message = message.replace("<SENDER>", commandSender.getName())
                .replace("<TARGET>", targetName)
                .replace("<LABEL>", label);

        if (_placeholders && commandSender instanceof Player player) message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);

        return message;
    }
}
