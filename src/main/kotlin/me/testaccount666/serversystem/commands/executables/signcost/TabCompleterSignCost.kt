package me.testaccount666.serversystem.commands.executables.signcost

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class TabCompleterSignCost : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String> {
        if (!hasCommandPermission(commandSender, "SignCost.Use", false)) return listOf()

        if (arguments.size != 1) return listOf()
        return _COST_TYPES.filter { it.startsWith(arguments[0], true) }
    }

    companion object {
        private val _COST_TYPES = setOf("none", "exp", "economy")
    }
}