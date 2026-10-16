package me.testaccount666.serversystem.userdata.listener

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.event.*
import org.bukkit.event.player.PlayerQuitEvent

class UserQuitListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onUserQuit(event: PlayerQuitEvent) {
        val user = getService<UserManager>().getUserOrNull(event.getPlayer().uniqueId) ?: run {
            ServerSystem.log.warning("(UserQuitListener) User '${event.getPlayer().name}' is not cached! This should not happen!")
            return
        }

        val offlineUser = user.offlineUser

        // We still save to prevent potential data loss
        if (user.isOfflineUser) ServerSystem.log.warning("User '${event.getPlayer().name}' is not an online user! This should not happen!")

        offlineUser.logoutPosition = event.getPlayer().location

        offlineUser.save()
        user.convertToOfflineUser()
    }
}