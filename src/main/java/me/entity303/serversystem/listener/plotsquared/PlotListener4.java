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
    public void OnPlayerEnterPlot(PlayerEnterPlotEvent event) {
        var timeDisabledFlag = event.getPlot().getFlag((TimeFlag.TIME_DISABLED));
        var player = (Player) event.getPlotPlayer().getPlatformPlayer();
        if (timeDisabledFlag > -9000000000L)
            PlotListener.TIME_MAP.put(player, timeDisabledFlag);
        else
            PlotListener.TIME_MAP.remove(player);
    }

    @Subscribe
    public void OnPlayerLeavePlot(PlayerLeavePlotEvent event) {
        var player = (Player) event.getPlotPlayer().getPlatformPlayer();
        PlotListener.TIME_MAP.remove(player);
    }
}
