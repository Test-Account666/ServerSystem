package me.entity303.serversystem.listener.plotsquared;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.HashMap;

public class PlotListener implements Listener {
    public static final HashMap<Player, Long> TIME_MAP = new HashMap<>();

    public PlotListener(ServerSystem plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void OnPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equalsIgnoreCase("PlotSquared")) {
            for (var player : PlotListener.TIME_MAP.keySet())
                player.resetPlayerTime();
            PlotListener.TIME_MAP.clear();
        }
    }
}
