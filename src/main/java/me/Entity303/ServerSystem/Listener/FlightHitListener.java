package me.Entity303.ServerSystem.Listener;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FlightHitListener extends ServerSystemCommand implements Listener {

    public FlightHitListener(ss plugin) {
        super(plugin);
    }

    @EventHandler
    public void onFlightHit(EntityDamageEvent e) {
        if (!this.plugin.isDisableFlightOnHit() && !this.plugin.isStopFlightOnHit()) {
            HandlerList.unregisterAll(this);
            return;
        }
        if (!(e.getEntity() instanceof Player)) return;
        if (this.isAllowed(e.getEntity(), "fly.bypassdamage", true)) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.STARVATION) return;
        Player player = (Player) e.getEntity();
        if (player.isFlying() || player.getAllowFlight()) {
            if (this.plugin.isStopFlightOnHit()) {
                e.getEntity().setFallDistance(0);
                player.setFlying(false);
                e.getEntity().setFallDistance(0);
            }
            if (this.plugin.isDisableFlightOnHit()) player.setAllowFlight(false);
        }
    }
}
