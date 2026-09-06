package me.testaccount666.serversystem.userdata.listener

import me.testaccount666.paperktx.scheduler.skedule.okkero.schedule
import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.event.*
import org.bukkit.event.player.PlayerJoinEvent

class UserJoinListener : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onUserJoin(event: PlayerJoinEvent) {
        val cachedUser = getService<UserManager>().getUserOrNull(event.getPlayer().uniqueId) ?: run {
            throw RuntimeException("Couldn't cache User '${event.getPlayer().name}'! This should not happen!")
        }
        if (cachedUser.isOnlineUser) return

        cachedUser.convertToOnlineUser()

        ServerSystem.instance.schedule {
            waitFor(10L)

            val user = cachedUser.offlineUser.takeIf { it.logoutPosition != null } ?: return@schedule
            event.getPlayer().teleport(user.logoutPosition!!)
        }
    }
}