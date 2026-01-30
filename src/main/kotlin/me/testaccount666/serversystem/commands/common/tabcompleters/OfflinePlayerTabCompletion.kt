package me.testaccount666.serversystem.commands.common.tabcompleters

import org.bukkit.Bukkit

object OfflinePlayerTabCompletion {
    fun getOfflinePlayerNames(vararg arguments: String): List<String> {
        val possibleCompletions = Bukkit.getOfflinePlayers().filter { !it.isOnline }.mapNotNull { it.name }

        return possibleCompletions.filter { it.startsWith(arguments[0], true) }
    }
}
