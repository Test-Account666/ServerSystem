package me.testaccount666.serversystem.listener.executables.joinquitnotifier

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.managers.messages.MessageManager.formatMessage
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class ListenerQuit : Listener {
    private val _modifyMessage: Boolean
    private val _sendMessage: Boolean
    private val _message: String
    private var _playSound: Boolean
    private var _sound: Sound? = null

    constructor() {
        val configManager = instance.registry.getService<ConfigurationManager>()
        val config = configManager.generalConfig
        _modifyMessage = config.getBoolean("Quit.Message.Enabled")
        _sendMessage = config.getBoolean("Quit.Message.SendMessage")
        _message = config.getString("Quit.Message.Message", "")!!
        _playSound = config.getBoolean("Quit.Sound.Enabled")
        if (!_playSound) {
            _sound = null
            return
        }

        var soundString = config.getString("Quit.Sound.Sound", "")!!

        var isMinecraft = true
        if (soundString.contains(":")) {
            val space = soundString.substring(0, soundString.indexOf(":"))
            soundString = soundString.drop(soundString.indexOf(":") + 1)
            isMinecraft = space.contentEquals("minecraft", true)
        }

        val soundKey = if (isMinecraft) NamespacedKey.minecraft(soundString) else NamespacedKey.fromString(soundString)
        if (soundKey == null) {
            _playSound = false
            log.warning("Failed to parse sound '${soundString}' for quit message!")
            return
        }

        _sound = Registry.SOUND_EVENT.get(soundKey)
        if (_sound == null) {
            _playSound = false
            log.warning("Failed to find sound '${soundString}' for quit message!")
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerQuitEvent) {
        handleMessage(event)
        playSound()
    }

    private fun playSound() {
        if (!_playSound) return

        Bukkit.getOnlinePlayers().forEach { everyone: Player? -> everyone!!.playSound(everyone, _sound!!, 1f, 1f) }
    }

    private fun handleMessage(event: PlayerQuitEvent) {
        if (!_modifyMessage) return

        if (!_sendMessage) {
            event.quitMessage = null
            return
        }
        val player = event.getPlayer()
        val user = instance.registry.getService<UserManager>().getUserOrNull(player)
        if (user == null) {
            log.warning("Couldn't cache User '${player.name}'! This should not happen!")
            return
        }
        if (user.isOfflineUser) {
            log.warning("User '${player.name}' is cached as Offline User! This should not happen!")
            return
        }
        val onlineUser = user.offlineUser as User

        val message = formatMessage(_message, onlineUser, null, null, false)
        event.quitMessage = message
    }
}
