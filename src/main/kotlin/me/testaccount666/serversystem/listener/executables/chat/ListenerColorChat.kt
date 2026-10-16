package me.testaccount666.serversystem.listener.executables.chat

import io.papermc.paper.event.player.AsyncChatEvent
import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.paperktx.extensions.ComponentExtensions.asString
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.managers.PermissionManager.hasPermission
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ListenerColorChat : Listener {
    private val _enabled = getService<ConfigurationManager>().generalConfig.getBoolean("Chat.ColorChat.Enabled")

    @EventHandler
    fun onColorChat(event: AsyncChatEvent) {
        if (!_enabled) return
        if (!hasPermission(event.getPlayer(), "Chat.ColorChat", false)) return
        event.message(event.message().asString().asComponent())
    }
}
