package me.testaccount666.serversystem.commands.executables.time

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command

class TabCompleterTime : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String> {
        if (!hasCommandPermission(commandSender, "Time.Use", false)) return listOf()

        if (command.name.equals("time", true)) {
            if (arguments.size <= 1) {
                val possibleCompletions = listOf("day", "night", "noon", "midnight")
                return possibleCompletions.filter { it.startsWith(arguments[0], true) }
            }

            if (arguments.size == 2) return handleWorldCompletions(commandSender, 1, *arguments)
            return listOf()
        }

        if (arguments.size == 1) return handleWorldCompletions(commandSender, 0, *arguments)
        return listOf()
    }

    private fun handleWorldCompletions(commandSender: User, index: Int, vararg arguments: String): List<String> {
        if (!hasCommandPermission(commandSender, "Time.World", false)) return listOf()

        val possibleCompletions = Bukkit.getWorlds().map { it.name }
        return possibleCompletions.filter { it.startsWith(arguments[index], true) }
    }
}
