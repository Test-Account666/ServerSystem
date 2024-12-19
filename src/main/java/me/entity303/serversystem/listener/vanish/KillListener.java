package me.entity303.serversystem.listener.vanish;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillListener implements Listener {

    protected final ServerSystem _plugin;

    public KillListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnEntityDeath(EntityDeathEvent event) {
        try {
            var killer = event.getEntity().getKiller();
            if (killer == null) return;
            if (this._plugin.GetVanish().IsVanish(killer)) {
                for (var all : Bukkit.getOnlinePlayers())
                    if (!this._plugin.GetPermissions().HasPermission(all, "vanish.see", true)) all.hidePlayer(killer);
            }
        } catch (Exception ignored) {

        }
    }

    @EventHandler
    public void OnPlayerDeath(PlayerDeathEvent event) {
        if (this._plugin.GetVanish().IsVanish(event.getEntity())) {
            event.setDeathMessage(null);
            for (var all : Bukkit.getOnlinePlayers())
                if (!this._plugin.GetPermissions().HasPermission(all, "vanish.see", true)) all.hidePlayer(event.getEntity());
        }
    }
}
