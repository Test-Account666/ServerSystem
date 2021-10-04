package me.Entity303.ServerSystem.Listener.PlotSquared;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.HashMap;

public class PlotListener implements Listener {
    public static final HashMap<Player, Long> TIME_MAP = new HashMap<>();

    public PlotListener(ss plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) {
        if (e.getPlugin().getName().equalsIgnoreCase("PlotSquared")) {
            for (Player player : PlotListener.TIME_MAP.keySet()) player.resetPlayerTime();
            PlotListener.TIME_MAP.clear();
        }
    }
}
