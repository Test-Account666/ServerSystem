package me.testaccount666.serversystem.clickablesigns.listener;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.PermissionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ListenerSignDestroy implements Listener {

    @EventHandler
    public void onBlockDestroy(BlockBreakEvent event) {
        var block = event.getBlock();
        var signManager = ServerSystem.Instance.getSignManager();
        var location = block.getLocation();
        var signOptional = signManager.getSignType(location);
        if (signOptional.isEmpty()) return;
        var signType = signOptional.get();

        if (PermissionManager.hasPermission(event.getPlayer(), signType.clickAction().getDestroyPermissionNode(), false)) return;

        event.setCancelled(true);
    }
}
