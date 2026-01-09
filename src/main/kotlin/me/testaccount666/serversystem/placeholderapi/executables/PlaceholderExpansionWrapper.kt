package me.testaccount666.serversystem.placeholderapi.executables

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.placeholderapi.PlaceholderManager
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.OfflinePlayer
import java.util.*

class PlaceholderExpansionWrapper : PlaceholderExpansion() {
    override fun getIdentifier(): String = "serversystem"

    override fun getAuthor(): String = instance.pluginMeta.authors.first()

    override fun getVersion(): String = instance.pluginMeta.version

    override fun persist(): Boolean = true

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (params.isBlank()) {
            log.warning("An invalid placeholder was requested!")
            return null
        }

        val split: Array<String> = params.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val identifier = split[0].lowercase(Locale.getDefault())

        val registry = instance.registry
        val placeholderManager = registry.getService<PlaceholderManager>()
        val placeholderOptional = placeholderManager.getPlaceholder(identifier)
        if (placeholderOptional.isEmpty) {
            log.warning("An unknown placeholder was requested! '${params}'")
            return null
        }

        val placeholder = placeholderOptional.get()

        val arguments: Array<String> = (if (split.size == 1) emptyArray<String>() else split.copyOfRange(1, split.size))

        if (player == null) return placeholder.execute(null, identifier, *arguments)

        val userManager = registry.getService<UserManager>()
        val userOptional = userManager.getUser(player.uniqueId)
        if (userOptional.isEmpty) return null
        val cachedUser = userOptional.get()
        val offlineUser = cachedUser.offlineUser

        return placeholder.execute(offlineUser, identifier, *arguments)
    }
}
