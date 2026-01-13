package me.testaccount666.serversystem.placeholderapi.executables

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.placeholderapi.PlaceholderManager
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.OfflinePlayer
import java.util.*

class PlaceholderExpansionWrapper : PlaceholderExpansion() {
    override fun getIdentifier() = "serversystem"
    override fun getAuthor() = instance.pluginMeta.authors.first()
    override fun getVersion() = instance.pluginMeta.version
    override fun persist() = true

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (params.isBlank()) {
            log.warning("An invalid placeholder was requested!")
            return null
        }

        val split = params.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val identifier = split[0].lowercase(Locale.getDefault())

        val registry = instance.registry
        val placeholderManager = registry.getService<PlaceholderManager>()
        val placeholder = placeholderManager.getPlaceholder(identifier)
        if (placeholder == null) {
            log.warning("An unknown placeholder was requested! '${params}'")
            return null
        }

        val arguments = (if (split.size == 1) emptyArray() else split.copyOfRange(1, split.size))

        if (player == null) return placeholder.execute(null, identifier, *arguments)

        val userManager = registry.getService<UserManager>()
        val cachedUser = userManager.getUserOrNull(player.uniqueId) ?: return null
        val offlineUser = cachedUser.offlineUser

        return placeholder.execute(offlineUser, identifier, *arguments)
    }
}
