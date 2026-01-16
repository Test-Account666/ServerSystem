package me.testaccount666.serversystem.commands.executables.speed

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class TabCompleterSpeed : ServerSystemTabCompleter {
    val numbers = (0 until 11).map { it.toString() }

    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        if (!PermissionManager.hasCommandPermission(commandSender, "Speed.Use")) return listOf()
        if (arguments.size == 2) return null

        if (arguments.size == 1) return numbers.filter { it.startsWith(arguments[0], true) }
        if (arguments.isEmpty()) return numbers

        return listOf()
    }
}