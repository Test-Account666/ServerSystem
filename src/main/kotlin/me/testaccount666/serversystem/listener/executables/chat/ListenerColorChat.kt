package me.testaccount666.serversystem.listener.executables.chat

import io.papermc.paper.event.player.AsyncChatEvent
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.managers.PermissionManager.hasPermission
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.utils.ComponentColor.Companion.componentToString
import me.testaccount666.serversystem.utils.ComponentColor.Companion.translateToComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ListenerColorChat : Listener {
    private val _enabled: Boolean

    init {
        val configManager = instance.registry.getService<ConfigurationManager>()
        val config = configManager.generalConfig
        _enabled = config.getBoolean("Chat.ColorChat.Enabled", false)
    }

    @EventHandler
    fun onColorChat(event: AsyncChatEvent) {
        if (!_enabled) return
        if (!hasPermission(event.getPlayer(), "Chat.ColorChat", false)) return
        val message = componentToString(event.message())

        event.message(translateToComponent(message))
    }
}
