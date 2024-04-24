package me.entity303.serversystem.listener.plotsquared;

import com.intellectualcrafters.plot.flag.Flags;
import com.plotsquared.bukkit.events.PlayerEnterPlotEvent;
import com.plotsquared.bukkit.events.PlayerLeavePlotEvent;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlotListener1 extends CommandUtils implements Listener {

    public PlotListener1(ServerSystem plugin) {
        super(plugin);
    }

    @SuppressWarnings("Guava")
    @EventHandler
    public void OnPlotEnter(PlayerEnterPlotEvent event) {
        if (event.getPlot().hasFlag(Flags.TIME)) {
            var optionalLong = event.getPlot().getFlag(Flags.TIME);
            if (optionalLong.isPresent())
                PlotListener.TIME_MAP.put(event.getPlayer(), optionalLong.get());
        }
    }

    @EventHandler
    public void OnPlotLeave(PlayerLeavePlotEvent event) {
        PlotListener.TIME_MAP.remove(event.getPlayer());
    }
}
