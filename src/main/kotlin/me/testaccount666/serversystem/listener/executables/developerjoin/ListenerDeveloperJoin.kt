package me.testaccount666.serversystem.listener.executables.developerjoin

import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.paperktx.scheduler.skedule.okkero.schedule
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*

class ListenerDeveloperJoin : Listener {
    private val _enabled = getService<ConfigurationManager>().generalConfig.getBoolean("DeveloperJoin.NotifyDeveloper.Enabled")

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (!_enabled) return

        val player = event.getPlayer()
        val uuid = player.uniqueId
        if (!_DEVELOPERS.contains(uuid)) return

        val message = "&#7FBF06This Server uses ServerSystem <3"

        instance.schedule {
            waitFor(20L)
            player.sendMessage(message.asComponent())
        }
    }

    companion object {
        private val _DEVELOPERS = listOf(
            UUID.fromString("6c3a735f-433c-4c5c-aae2-3211d7e7acdc"),
            UUID.fromString("94d7b2cf-29d4-48c3-924e-c56ac009823d")
        )
    }
}
