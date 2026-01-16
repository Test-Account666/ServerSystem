package me.testaccount666.serversystem.commands.executables.repair

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class TabCompleterRepair : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String> {
        if (!hasCommandPermission(commandSender, "Repair.Use", false)) return listOf()
        if (arguments.size != 1) return listOf()

        val potentialCompletions = listOf("all", "*", "armor", "hand", "offhand", "inventory")
        return potentialCompletions.filter { it.startsWith(arguments[0], true) }
    }
}
