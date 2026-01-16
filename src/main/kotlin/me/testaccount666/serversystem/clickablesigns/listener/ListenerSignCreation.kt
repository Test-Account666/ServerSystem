package me.testaccount666.serversystem.clickablesigns.listener

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.clickablesigns.SignType
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.ComponentColor.Companion.componentToString
import org.bukkit.Bukkit
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent

class ListenerSignCreation : Listener {
    @EventHandler
    fun onSignCreation(event: SignChangeEvent) {
        val firstLine = event.lines().firstOrNull() ?: return
        val firstLineString = componentToString(firstLine)
        if (!firstLineString.contains("[") || !firstLineString.contains("]")) return

        val key = firstLineString.substring(firstLineString.indexOf("[") + 1, firstLineString.indexOf("]"))
        val signType = SignType.getSignTypeByKey(key) ?: return

        val block = event.getBlock()
        val player = event.player

        val registry = instance.registry
        val userManager = registry.getService<UserManager>()

        val user = userManager.getUserOrNull(player) ?: return
        if (user.isOfflineUser) return
        val onlineUser = user.offlineUser as User

        Bukkit.getScheduler().runTaskLater(instance, Runnable {
            val sign = block.state as Sign
            signType.configurator.execute(onlineUser, sign)
        }, 1L)
    }
}
