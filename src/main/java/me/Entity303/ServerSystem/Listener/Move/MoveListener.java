package me.Entity303.ServerSystem.Listener.Move;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {
    private final ss plugin;

    public MoveListener(ss plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (this.plugin.getTeleportMap().containsKey(e.getPlayer())) {
            double x = Math.max(e.getTo().getX(), e.getFrom().getX()) - Math.min(e.getTo().getX(), e.getFrom().getX());
            double y = Math.max(e.getTo().getY(), e.getFrom().getY()) - Math.min(e.getTo().getY(), e.getFrom().getY());
            double z = Math.max(e.getTo().getZ(), e.getFrom().getZ()) - Math.min(e.getTo().getZ(), e.getFrom().getZ());
            if (x > 0.1 || x < -0.1 || y > 0.2 || y < -0.2 || z > 0.1 || z < -0.1) {
                this.plugin.getTeleportMap().get(e.getPlayer()).cancel();
                this.plugin.getTeleportMap().remove(e.getPlayer());
                e.getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.Teleportation.Failed")));
            }
        }

    }
}
