package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FlightHitListener extends CommandUtils implements Listener {

    public FlightHitListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void onFlightHit(EntityDamageEvent e) {
        if (!this.plugin.isDisableFlightOnHit() && !this.plugin.isStopFlightOnHit()) {
            HandlerList.unregisterAll(this);
            return;
        }

        if (!(e.getEntity() instanceof Player player))
            return;

        if (this.plugin.getPermissions().hasPermission(e.getEntity(), "fly.bypassdamage", true))
            return;

        if (e.getCause() == EntityDamageEvent.DamageCause.STARVATION)
            return;

        if (player.isFlying() || player.getAllowFlight()) {
            if (this.plugin.isStopFlightOnHit()) {
                e.getEntity().setFallDistance(0);
                player.setFlying(false);
                e.getEntity().setFallDistance(0);
            }
            if (this.plugin.isDisableFlightOnHit())
                player.setAllowFlight(false);
        }
    }
}
