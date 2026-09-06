package me.testaccount666.serversystem.clickablesigns.listener

import me.testaccount666.paperktx.extensions.ComponentExtensions.asString
import me.testaccount666.paperktx.scheduler.skedule.okkero.schedule
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.clickablesigns.SignType
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent

class ListenerSignCreation : Listener {
    @EventHandler
    fun onSignCreation(event: SignChangeEvent) {
        val firstLine = event.lines().firstOrNull() ?: return
        val firstLineString = firstLine.asString()
        if (!firstLineString.contains("[") || !firstLineString.contains("]")) return

        val key = firstLineString.substring(firstLineString.indexOf("[") + 1, firstLineString.indexOf("]"))
        val signType = SignType.getSignTypeByKey(key) ?: return

        val block = event.getBlock()

        val user = getService<UserManager>().getUserOrNull(event.player) ?: return
        if (user.isOfflineUser) return

        instance.schedule {
            waitFor(1L)
            val sign = block.state as Sign
            signType.configurator.execute(user.onlineUser, sign)
        }
    }
}
