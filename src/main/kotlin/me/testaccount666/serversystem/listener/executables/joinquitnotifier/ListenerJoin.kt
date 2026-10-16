package me.testaccount666.serversystem.listener.executables.joinquitnotifier

import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.managers.messages.MessageManager.formatMessage
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ListenerJoin : Listener {
    private val _modifyMessage: Boolean
    private val _sendMessage: Boolean
    private val _message: String
    private var _playSound: Boolean
    private var _sound: Sound? = null

    constructor() {
        val config = getService<ConfigurationManager>().generalConfig
        _modifyMessage = config.getBoolean("Join.Message.Enabled")
        _sendMessage = config.getBoolean("Join.Message.SendMessage")
        _message = config.getValue("Join.Message.Message", "")
        _playSound = config.getBoolean("Join.Sound.Enabled")
        if (!_playSound) {
            _sound = null
            return
        }

        var soundString = config.getValue("Join.Sound.Sound", "")

        var isMinecraft = true
        if (soundString.contains(":")) {
            val space = soundString.substring(0, soundString.indexOf(":"))
            soundString = soundString.drop(soundString.indexOf(":") + 1)
            isMinecraft = space.contentEquals("minecraft", true)
        }

        val soundKey = (if (isMinecraft) NamespacedKey.minecraft(soundString)
        else NamespacedKey.fromString(soundString)) ?: run {
            _playSound = false
            log.warning("Failed to parse sound '${soundString}' for join message!")
            return
        }

        _sound = Registry.SOUND_EVENT.get(soundKey) ?: let {
            _playSound = false
            log.warning("Failed to find sound '${soundString}' for join message!")
            null
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        handleMessage(event)
        playSound()
    }

    private fun playSound() {
        if (!_playSound) return

        Bukkit.getOnlinePlayers().forEach { everyone -> everyone.playSound(everyone, _sound!!, 1f, 1f) }
    }

    private fun handleMessage(event: PlayerJoinEvent) {
        if (!_modifyMessage) return

        if (!_sendMessage) {
            event.joinMessage = null
            return
        }
        val player = event.getPlayer()
        val user = getService<UserManager>().getUserOrNull(player) ?: run {
            log.warning("Couldn't cache User '${player.name}'! This should not happen!")
            return
        }
        if (user.isOfflineUser) {
            log.warning("User '${player.name}' is cached as Offline User! This should not happen!")
            return
        }

        formatMessage(_message, user.onlineUser, null, null, false).asComponent().also(event::joinMessage)
    }
}
