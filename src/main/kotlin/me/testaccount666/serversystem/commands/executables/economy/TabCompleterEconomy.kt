package me.testaccount666.serversystem.commands.executables.economy

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class TabCompleterEconomy : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        if (!hasCommandPermission(commandSender, "Economy.Use", false)) return listOf()

        if (arguments.size == 1) {
            val potentialCompletions = mutableListOf("set", "take", "give")
            val completions = potentialCompletions.filter { it.startsWith(arguments[0], true) }.toList()
            return completions
        }

        if (arguments.size == 2) return null
        return listOf()
    }
}
