package me.Entity303.ServerSystem.Listener;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BackListener implements Listener {
    private final ss plugin;

    public BackListener(ss plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        this.plugin.getBackloc().put(e.getPlayer(), e.getFrom());
        this.plugin.getBackreason().put(e.getPlayer(), "Teleport");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        this.plugin.getBackloc().put(e.getEntity(), e.getEntity().getLocation());
        this.plugin.getBackreason().put(e.getEntity(), "Death");
    }
}
