package me.entity303.serversystem.listener.vanish;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillListener extends MessageUtils implements Listener {

    public KillListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKill(EntityDeathEvent e) {
        try {
            Player killer = e.getEntity().getKiller();
            if (killer == null) return;
            if (this.plugin.getVanish().isVanish(killer)) for (Player all : Bukkit.getOnlinePlayers())
                if (!this.isAllowed(all, "vanish.see", true)) all.hidePlayer(killer);
        } catch (Exception ignored) {

        }
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        if (this.plugin.getVanish().isVanish(e.getEntity())) {
            e.setDeathMessage(null);
            for (Player all : Bukkit.getOnlinePlayers())
                if (!this.isAllowed(all, "vanish.see", true)) all.hidePlayer(e.getEntity());
        }
    }
}
