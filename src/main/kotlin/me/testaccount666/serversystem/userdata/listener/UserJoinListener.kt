package me.testaccount666.serversystem.userdata.listener

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class UserJoinListener : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onUserJoin(event: PlayerJoinEvent) {
        val userManager = ServerSystem.instance.registry.getService<UserManager>()
        val cachedUser = userManager.getUserOrNull(event.getPlayer().uniqueId)
        if (cachedUser == null) throw RuntimeException("Couldn't cache User '${event.getPlayer().name}'! This should not happen!")
        if (cachedUser.isOnlineUser) return

        cachedUser.convertToOnlineUser()

        Bukkit.getScheduler().runTaskLater(ServerSystem.instance, Runnable {
            val user = cachedUser.offlineUser
            if (user.logoutPosition == null) return@Runnable
            event.getPlayer().teleport(user.logoutPosition!!)
        }, 10L)
    }
}