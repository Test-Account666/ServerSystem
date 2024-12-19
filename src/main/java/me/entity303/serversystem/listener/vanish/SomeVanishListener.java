package me.entity303.serversystem.listener.vanish;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class SomeVanishListener implements Listener {

    protected final ServerSystem _plugin;

    public SomeVanishListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnVehicleCollision(VehicleEntityCollisionEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (this._plugin.GetVanish().IsVanish(player)) {
            event.setCancelled(true);
            event.setPickupCancelled(true);
            event.setCollisionCancelled(true);
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void OnHangingBreak(HangingBreakByEntityEvent event) {
        var entity = event.getRemover();
        if (!(entity instanceof Player player)) return;
        if (this._plugin.GetVanish().IsVanish(player) && this._plugin.GetVanish().GetAllowInteract().contains(player) &&
            this._plugin.GetCommandManager().IsInteractActive()) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void OnPlayerChangedWorld(PlayerChangedWorldEvent event) {
        if (this._plugin.GetVanish().IsVanish(event.getPlayer())) {
            this._plugin.GetVanish().SetVanish(true, event.getPlayer());
        } else {
            var vanished = this._plugin.GetVanish().GetVanishList();
            for (var uuid : vanished)
                if (Bukkit.getOfflinePlayer(uuid).isOnline()) this._plugin.GetVanish().SetVanish(true, Bukkit.getPlayer(uuid));
        }

    }
}
