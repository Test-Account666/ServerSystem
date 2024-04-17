package me.entity303.serversystem.listener.plotsquared;

import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.events.PlayerEnterPlotEvent;
import com.plotsquared.core.events.PlayerLeavePlotEvent;
import com.plotsquared.core.plot.flag.implementations.TimeFlag;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage") public class PlotListener4 {

    public PlotListener4() {
        var plotAPI = new PlotAPI();
        plotAPI.registerListener(this);
    }

    @Subscribe
    public void onPlayerEnterPlot(PlayerEnterPlotEvent e) {
        var l = e.getPlot().getFlag((TimeFlag.TIME_DISABLED));
        var player = (Player) e.getPlotPlayer().getPlatformPlayer();
        if (l > -9000000000L)
            PlotListener.TIME_MAP.put(player, l);
        else
            PlotListener.TIME_MAP.remove(player);
    }

    @Subscribe
    public void onPlayerLeavePlot(PlayerLeavePlotEvent e) {
        var player = (Player) e.getPlotPlayer().getPlatformPlayer();
        PlotListener.TIME_MAP.remove(player);
    }
}
