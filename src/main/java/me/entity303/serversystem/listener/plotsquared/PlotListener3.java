package me.entity303.serversystem.listener.plotsquared;

import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.events.PlayerEnterPlotEvent;
import com.plotsquared.core.events.PlayerLeavePlotEvent;
import com.plotsquared.core.plot.flag.implementations.TimeFlag;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PlotListener3 {

    public PlotListener3() {
        Class clazz = null;
        try {
            clazz = Class.forName("com.plotsquared.core.api.PlotAPI");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Object plotAPI = null;

        try {
            plotAPI = clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        Method registerListenerMethod = null;
        try {
            registerListenerMethod = clazz.getDeclaredMethod("registerListener", Object.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        try {
            registerListenerMethod.invoke(plotAPI, this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onPlayerEnterPlot(PlayerEnterPlotEvent e) {
        Long l = e.getPlot().getFlag((TimeFlag.TIME_DISABLED));
        Player player = (Player) e.getPlotPlayer().getPlatformPlayer();
        if (l > -9000000000L)
            PlotListener.TIME_MAP.put(player, l);
        else
            PlotListener.TIME_MAP.remove(player);
    }

    @Subscribe
    public void onPlayerLeavePlot(PlayerLeavePlotEvent e) {
        Player player = (Player) e.getPlotPlayer().getPlatformPlayer();
        PlotListener.TIME_MAP.remove(player);
    }
}
