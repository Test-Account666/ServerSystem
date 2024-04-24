package me.entity303.serversystem.actionbar;

import me.entity303.serversystem.utils.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import static net.md_5.bungee.api.ChatMessageType.ACTION_BAR;

public class ActionBar implements IActionBar {

    @Override
    public void SendActionBar(Player player, String message) {
        var coloredMessage = ChatColor.TranslateAlternateColorCodes('&', message);
        var chatComponent = new TextComponent(coloredMessage);

        player.spigot().sendMessage(ACTION_BAR, chatComponent);

    }
}
