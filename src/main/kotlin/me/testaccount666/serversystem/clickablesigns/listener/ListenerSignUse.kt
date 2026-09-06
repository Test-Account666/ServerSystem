package me.testaccount666.serversystem.clickablesigns.listener

import me.testaccount666.serversystem.clickablesigns.SignManager
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class ListenerSignUse : Listener {
    @EventHandler
    fun onSignUse(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        val clickedBlock = event.clickedBlock ?: return
        val sign = clickedBlock.state as? Sign ?: return

        val location = clickedBlock.location
        val signType = getService<SignManager>().getSignType(location) ?: return

        val player = event.getPlayer()
        val cachedUser = getService<UserManager>().getUserOrNull(player) ?: return
        if (cachedUser.isOfflineUser) return

        event.isCancelled = true
        signType.clickAction.execute(cachedUser.onlineUser, sign)
    }
}
