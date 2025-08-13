package me.testaccount666.serversystem.listener.executables.developerjoin;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.config.ConfigurationManager;
import me.testaccount666.serversystem.utils.ComponentColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.UUID;

public class ListenerDeveloperJoin implements Listener {
    private static final List<UUID> _DEVELOPERS = List.of(
            UUID.fromString("6c3a735f-433c-4c5c-aae2-3211d7e7acdc"),
            UUID.fromString("94d7b2cf-29d4-48c3-924e-c56ac009823d")
    );
    private final boolean _enabled;

    public ListenerDeveloperJoin() {
        var configManager = ServerSystem.Instance.getRegistry().getService(ConfigurationManager.class);
        _enabled = configManager.getGeneralConfig().getBoolean("DeveloperJoin.NotifyDeveloper.Enabled");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!_enabled) return;

        var player = event.getPlayer();
        var uuid = player.getUniqueId();
        if (!_DEVELOPERS.contains(uuid)) return;

        var message = "&#7FBF06This Server uses ServerSystem <3";
        var messageComponent = ComponentColor.translateToComponent(message);
        Bukkit.getScheduler().runTaskLater(ServerSystem.Instance, () -> player.sendMessage(messageComponent), 20);
    }
}
