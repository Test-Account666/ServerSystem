package me.testaccount666.serversystem.clickablesigns.listener

import me.testaccount666.serversystem.clickablesigns.SignManager
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.managers.PermissionManager.hasPermission
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class ListenerSignDestroy : Listener {
    @EventHandler
    fun onBlockDestroy(event: BlockBreakEvent) {
        val signType = getService<SignManager>().getSignType(event.getBlock().location) ?: return
        if (hasPermission(event.player, signType.clickAction.destroyPermissionNode, false)) return

        event.isCancelled = true
    }
}
