package me.testaccount666.serversystem.listener.executables.chat.prefixchat

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ListenerPrefixChat : Listener {
    private val _chatVaultAPI: ChatVaultAPI?
    private val _enabled: Boolean

    constructor() {
        val enabled = getService<ConfigurationManager>().generalConfig.getBoolean("Chat.PrefixChat.Enabled", false)

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

        val user = getService<UserManager>().getUserOrNull(player) ?: run {
            log.warning("Couldn't cache User '${player.name}'! This should not happen!")
            return
        }
        if (user.isOfflineUser) {
            log.warning("User '${player.name}' is cached as Offline User! This should not happen!")
            return
        }

        val chatFormat = user.onlineUser.generalMsg("ChatFormat") {
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
        }.takeUnless { it.isBlank() } ?: return

        event.format = chatFormat
    }
}
