package me.entity303.serversystem.actionbar;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static net.md_5.bungee.api.ChatMessageType.ACTION_BAR;

public class ActionBar extends IActionBar {
    private final ServerSystem plugin;
    private Method getHandleMethod = null;
    private Method sendPacketMethod = null;
    private Field playerConnectionField = null;

    public ActionBar() {
        this.plugin = ServerSystem.getPlugin(ServerSystem.class);
    }

    @Override
    public Method getGetHandleMethod() {
        return this.getHandleMethod;
    }

    @Override
    public Field getPlayerConnectionField() {
        return this.playerConnectionField;
    }

    @Override
    public Method getSendPacketMethod() {
        return this.sendPacketMethod;
    }

    @Override
    public void sendActionBar(Player player, String message) {
        var chatComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));

        player.spigot().sendMessage(ACTION_BAR, chatComponent);

    }
}
