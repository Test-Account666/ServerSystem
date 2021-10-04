package me.Entity303.ServerSystem.Listener.Vanish;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillListener extends ServerSystemCommand implements Listener {

    public KillListener(ss plugin) {
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
