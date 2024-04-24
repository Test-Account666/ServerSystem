package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneListener extends CommandUtils implements Listener {

    public RedstoneListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void OnRedstone(BlockRedstoneEvent event) {
        event.setNewCurrent(0);
    }

}
