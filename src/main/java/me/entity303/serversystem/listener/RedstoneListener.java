package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneListener extends MessageUtils implements Listener {

    public RedstoneListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent e) {
        e.setNewCurrent(0);
    }

}
