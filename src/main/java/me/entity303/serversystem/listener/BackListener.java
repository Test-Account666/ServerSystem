package me.entity303.serversystem.listener;

import me.entity303.serversystem.commands.executable.BackCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BackListener implements Listener {
    private final ServerSystem _plugin;

    public BackListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnTeleport(PlayerTeleportEvent event) {
        var player = event.getPlayer();
        var from = event.getFrom();
        this._plugin.GetBackloc().put(player, from);
        this._plugin.GetBackReason().put(player, BackCommand.BACK_REASON_TELEPORT);
    }

    @EventHandler
    public void OnDeath(PlayerDeathEvent event) {
        var player = event.getEntity();
        var playerLocation = player.getLocation();
        this._plugin.GetBackloc().put(player, playerLocation);
        this._plugin.GetBackReason().put(player, BackCommand.BACK_REASON_DEATH);
    }
}
