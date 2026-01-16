package me.testaccount666.serversystem.commands.executables.seen

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command

class TabCompleterSeen : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        if (!hasCommandPermission(commandSender, "Seen.Use", false)) return null

        if (arguments.size != 1) return null
        val potentialCompletions = Bukkit.getOfflinePlayers().mapNotNull { it.name }

        return potentialCompletions.filter { it.startsWith(arguments[0], true) }
    }
}
