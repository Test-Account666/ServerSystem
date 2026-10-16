package me.testaccount666.serversystem.placeholderapi.executables

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.placeholderapi.PlaceholderManager
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.OfflinePlayer

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

        val split = params.split("_").dropLastWhile(String::isEmpty)
        val identifier = split.first().lowercase()

        val placeholder = getService<PlaceholderManager>().getPlaceholder(identifier) ?: run {
            log.warning("An unknown placeholder was requested! '${params}'")
            return null
        }

        val arguments = split.drop(1).toTypedArray()

        if (player == null) return placeholder.execute(null, identifier, *arguments)
        val cachedUser = getService<UserManager>().getUserOrNull(player.uniqueId) ?: return null
        return placeholder.execute(cachedUser.offlineUser, identifier, *arguments)
    }
}
