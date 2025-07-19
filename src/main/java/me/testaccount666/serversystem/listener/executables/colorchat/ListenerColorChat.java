package me.testaccount666.serversystem.listener.executables.colorchat;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.utils.ComponentColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ListenerColorChat implements Listener {
    private final boolean _enabled;

    public ListenerColorChat() {
        var config = ServerSystem.Instance.getConfigManager().getGeneralConfig();
        _enabled = config.getBoolean("Chat.ColorChat.Enabled");
    }

    @EventHandler
    public void onColorChat(AsyncChatEvent event) {
        if (!_enabled) return;
        if (!PermissionManager.hasPermission(event.getPlayer(), "Chat.ColorChat", false)) return;
        var message = ComponentColor.componentToString(event.message());

        event.message(ComponentColor.translateToComponent(message));
    }
}
