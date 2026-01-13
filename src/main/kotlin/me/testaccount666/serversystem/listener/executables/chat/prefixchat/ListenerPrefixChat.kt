package me.testaccount666.serversystem.listener.executables.chat.prefixchat

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ListenerPrefixChat : Listener {
    private val _chatVaultAPI: ChatVaultAPI?
    private val _enabled: Boolean

    constructor() {
        val configManager = instance.registry.getService<ConfigurationManager>()
        val enabled = configManager.generalConfig.getBoolean("Chat.PrefixChat.Enabled", false)

        if (!ChatVaultAPI.isVaultInstalled || !enabled) {
            _chatVaultAPI = null
            _enabled = false
            return
        }

        _chatVaultAPI = ChatVaultAPI()
        _enabled = _chatVaultAPI.setupChat()
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        if (!_enabled) return

        val player = event.getPlayer()
        var prefix = _chatVaultAPI?.chat?.getPlayerPrefix(player) ?: ""
        var suffix = _chatVaultAPI?.chat?.getPlayerSuffix(player) ?: ""

        prefix = prefix.replace("%", "%%")
        suffix = suffix.replace("%", "%%")

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

        val chatFormat = general("ChatFormat", onlineUser) {
            sender($$"%1$s")
            prefix(false)
            send(false)
            blankError(true)
            preModifier {
                it.replace("%", "%%")
                    .replace("<PREFIX>", prefix)
                    .replace("<SUFFIX>", suffix)
                    .replace("<MESSAGE>", $$"%2$s")
            }
        }.build()
        if (chatFormat.isEmpty()) return

        event.format = chatFormat
    }
}
