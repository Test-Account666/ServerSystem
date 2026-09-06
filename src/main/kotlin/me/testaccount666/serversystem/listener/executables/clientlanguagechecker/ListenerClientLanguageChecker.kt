package me.testaccount666.serversystem.listener.executables.clientlanguagechecker

import com.destroystokyo.paper.ClientOption
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ListenerClientLanguageChecker : Listener {
    private val _useClientLanguage: Boolean

    init {
        val configManager = getService<ConfigurationManager>()
        _useClientLanguage = configManager.generalConfig.getBoolean("Language.UseClientLanguage")
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val user = getService<UserManager>().getUserOrNull(event.getPlayer()) ?: return
        if (user.isOfflineUser) return
        val onlineUser = user.onlineUser

        if (!onlineUser.isUsesDefaultLanguage) return

        //TODO: Use default language
        if (!_useClientLanguage) return

        val locale = event.getPlayer().getClientOption(ClientOption.LOCALE)
        var language = "English"

        if (locale.startsWith("en_")) language = "English"
        if (locale.startsWith("de_")) language = "German"
        if (locale.startsWith("sl_")) language = "Slovene"

        onlineUser.playerLanguage = language.lowercase()
    }
}
