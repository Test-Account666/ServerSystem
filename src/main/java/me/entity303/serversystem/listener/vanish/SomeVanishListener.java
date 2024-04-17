package me.entity303.serversystem.listener.vanish;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class SomeVanishListener extends CommandUtils implements Listener {

    public SomeVanishListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void onVehicleCollision(VehicleEntityCollisionEvent e) {
        if (!(e.getEntity() instanceof Player player))
            return;
        if (this.plugin.getVanish().isVanish(player)) {
            e.setCancelled(true);
            e.setPickupCancelled(true);
            e.setCollisionCancelled(true);
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onHangingBreak(HangingBreakByEntityEvent e) {
        var entity = e.getRemover();
        if (!(entity instanceof Player player))
            return;
        if (this.plugin.getVanish().isVanish(player) && this.plugin.getVanish().getAllowInteract().contains(player) &&
            this.plugin.getCommandManager().isInteractActive())
            e.setCancelled(true);

    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        if (this.plugin.getVanish().isVanish(e.getPlayer()))
            this.plugin.getVanish().setVanish(true, e.getPlayer());
        else {
            var vanished = this.plugin.getVanish().getVanishList();
            for (var uuid : vanished)
                if (Bukkit.getOfflinePlayer(uuid).isOnline())
                    this.plugin.getVanish().setVanish(true, Bukkit.getPlayer(uuid));
        }

    }
}
