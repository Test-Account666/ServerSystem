package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class GodListener implements Listener {

    protected final ServerSystem _plugin;

    public GodListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (this._plugin.GetGodList().contains(((Player) event.getEntity()).getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void OnDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (this._plugin.GetGodList().contains(((Player) event.getEntity()).getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void OnFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (this._plugin.GetGodList().contains(((Player) event.getEntity()).getPlayer())) {
            event.getEntity().setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnEntityTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() == null)
            return;

        if (!(event.getTarget() instanceof Player player))
            return;

        if (this._plugin.GetGodList().contains(player))
            event.setTarget(null);
    }
}
