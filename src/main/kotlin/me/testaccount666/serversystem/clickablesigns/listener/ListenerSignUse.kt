package me.testaccount666.serversystem.clickablesigns.listener

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.clickablesigns.SignManager
import me.testaccount666.serversystem.userdata.User
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

        val registry = instance.registry
        val signManager = registry.getService<SignManager>()
        val location = clickedBlock.location
        val signType = signManager.getSignType(location) ?: return

        val player = event.getPlayer()
        val userManager = registry.getService<UserManager>()
        val cachedUser = userManager.getUserOrNull(player) ?: return
        if (cachedUser.isOfflineUser) return
        val user = cachedUser.offlineUser as User

        event.isCancelled = true
        signType.clickAction().execute(user, sign)
    }
}
