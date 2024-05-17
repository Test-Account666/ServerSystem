package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneListener implements Listener {

    protected final ServerSystem _plugin;

    public RedstoneListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnRedstone(BlockRedstoneEvent event) {
        event.setNewCurrent(0);
    }

}
