package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FlightHitListener implements Listener {

    protected final ServerSystem _plugin;

    public FlightHitListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnFlightHit(EntityDamageEvent event) {
        if (!this._plugin.IsDisableFlightOnHit() && !this._plugin.IsStopFlightOnHit()) {
            HandlerList.unregisterAll(this);
            return;
        }

        if (!(event.getEntity() instanceof Player player)) return;

        if (this._plugin.GetPermissions().HasPermission(event.getEntity(), "fly.bypassdamage", true)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.STARVATION) return;

        if (player.isFlying() || player.getAllowFlight()) {
            if (this._plugin.IsStopFlightOnHit()) {
                event.getEntity().setFallDistance(0);
                player.setFlying(false);
                event.getEntity().setFallDistance(0);
            }
            if (this._plugin.IsDisableFlightOnHit()) player.setAllowFlight(false);
        }
    }
}
