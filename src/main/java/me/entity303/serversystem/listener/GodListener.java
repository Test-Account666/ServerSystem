package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class GodListener extends MessageUtils implements Listener {

    public GodListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (this.plugin.getGodList().contains(((Player) e.getEntity()).getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (this.plugin.getGodList().contains(((Player) e.getEntity()).getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (this.plugin.getGodList().contains(((Player) e.getEntity()).getPlayer())) {
            ((Player) e.getEntity()).setFoodLevel(20);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent e) {
        if (e.getTarget() == null) return;
        if (!(e.getTarget() instanceof Player)) return;
        Player player = (Player) e.getTarget();
        if (this.plugin.getGodList().contains(player)) e.setTarget(null);
    }
}
