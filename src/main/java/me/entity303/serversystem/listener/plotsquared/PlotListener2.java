package me.entity303.serversystem.listener.plotsquared;

import com.github.intellectualsites.plotsquared.bukkit.events.PlayerEnterPlotEvent;
import com.github.intellectualsites.plotsquared.bukkit.events.PlayerLeavePlotEvent;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.intellectualsites.plotsquared.plot.flag.Flags.TIME;

public class PlotListener2 implements Listener {

    protected final ServerSystem _plugin;

    public PlotListener2(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnPlotEnter(PlayerEnterPlotEvent event) {
        if (event.getPlot().hasFlag(TIME))
            if (event.getPlot().getFlag(TIME).isPresent())
                PlotListener.TIME_MAP.put(event.getPlayer(), event.getPlot().getFlag(TIME).get());
    }

    @EventHandler
    public void OnPlotLeave(PlayerLeavePlotEvent event) {
        PlotListener.TIME_MAP.remove(event.getPlayer());
    }
}
