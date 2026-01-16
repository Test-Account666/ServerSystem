package me.testaccount666.serversystem.commands.executables.home.admin

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.userdata.home.Home
import org.bukkit.command.Command

class TabCompleterAdminHome : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        val commandName = command.name.substring("admin".length)

        if (commandName.equals("sethome", true)) return handleSetHomeCommand(commandSender, *arguments)

        if (commandName.equals("deletehome", true)) return handleDeleteHomeCommand(commandSender, *arguments)

        return handleHomeCommand(commandSender, *arguments)
    }

    private fun handleHomeCommand(commandSender: User, vararg arguments: String): List<String>? {
        if (!hasCommandPermission(commandSender, "AdminHome.Use", false)) return null
        if (arguments.size <= 1) return null

        return handleHomeCompletion(*arguments)
    }

    private fun handleSetHomeCommand(commandSender: User, vararg arguments: String): List<String>? {
        if (!hasCommandPermission(commandSender, "AdminHome.Set", false)) return null
        if (arguments.size <= 1) return null

        return mutableListOf()
    }

    private fun handleDeleteHomeCommand(commandSender: User, vararg arguments: String): List<String>? {
        if (!hasCommandPermission(commandSender, "AdminHome.Delete", false)) return null
        if (arguments.size <= 1) return null

        return handleHomeCompletion(*arguments)
    }

    private fun handleHomeCompletion(vararg arguments: String): List<String> {
        if (arguments.size != 2) return listOf()

        val targetCachedUser = instance.registry.getService<UserManager>().getUserOrNull(arguments[0]) ?: return listOf()
        val targetUser = targetCachedUser.offlineUser

        val homeManager = targetUser.homeManager

        val potentialCompletions = homeManager.homes.map(Home::displayName)
        return potentialCompletions.filter { it.startsWith(arguments[1], true) }
    }
}
