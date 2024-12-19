package me.entity303.serversystem.listener.move;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {
    private final ServerSystem _plugin;

    public MoveListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnMove(PlayerMoveEvent event) {
        if (!this._plugin.GetTeleportMap().containsKey(event.getPlayer())) return;

        var xCoordinateDifference = Math.max(event.getTo().getX(), event.getFrom().getX()) - Math.min(event.getTo().getX(), event.getFrom().getX());
        var yCoordinateDifference = Math.max(event.getTo().getY(), event.getFrom().getY()) - Math.min(event.getTo().getY(), event.getFrom().getY());
        var zCoordinateDifference = Math.max(event.getTo().getZ(), event.getFrom().getZ()) - Math.min(event.getTo().getZ(), event.getFrom().getZ());
        if (!this.ShouldCancelTeleport(xCoordinateDifference, yCoordinateDifference, zCoordinateDifference)) return;

        this._plugin.GetTeleportMap().get(event.getPlayer()).cancel();
        this._plugin.GetTeleportMap().remove(event.getPlayer());
        event.getPlayer()
             .sendMessage(this._plugin.GetMessages().GetPrefix() + ChatColor.TranslateAlternateColorCodes('&', this._plugin.GetMessages()
                                                                                                                           .GetConfiguration()
                                                                                                                           .GetString("Messages.Misc" + ".Teleportation" +
                                                                                                                                      ".Failed")));

    }

    private boolean ShouldCancelTeleport(double xCoordinateDifference, double yCoordinateDifference, double zCoordinateDifference) {
        return xCoordinateDifference > 0.1 || xCoordinateDifference < -0.1 || yCoordinateDifference > 0.2 || yCoordinateDifference < -0.2 ||
               zCoordinateDifference > 0.1 || zCoordinateDifference < -0.1;
    }
}
