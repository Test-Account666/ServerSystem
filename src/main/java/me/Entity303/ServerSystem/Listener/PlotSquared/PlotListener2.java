package me.Entity303.ServerSystem.Listener.PlotSquared;

import com.github.intellectualsites.plotsquared.bukkit.events.PlayerEnterPlotEvent;
import com.github.intellectualsites.plotsquared.bukkit.events.PlayerLeavePlotEvent;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.intellectualsites.plotsquared.plot.flag.Flags.TIME;

public class PlotListener2 extends ServerSystemCommand implements Listener {

    public PlotListener2(ss plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlotEnter(PlayerEnterPlotEvent e) {
        if (e.getPlot().hasFlag(TIME))
            if (e.getPlot().getFlag(TIME).isPresent())
                PlotListener.TIME_MAP.put(e.getPlayer(), e.getPlot().getFlag(TIME).get());
    }

    @EventHandler
    public void onPlotLeave(PlayerLeavePlotEvent e) {
        PlotListener.TIME_MAP.remove(e.getPlayer());
    }
}
