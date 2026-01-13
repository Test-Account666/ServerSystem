package me.testaccount666.serversystem.commands.executables.home.user

import me.testaccount666.serversystem.commands.executables.home.admin.TabCompleterAdminHome
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.home.Home
import org.bukkit.command.Command

class TabCompleterHome : ServerSystemTabCompleter {
    private val _tabCompleterAdminHome = TabCompleterAdminHome()

    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        if (command.name.startsWith("admin", true)) return _tabCompleterAdminHome.tabComplete(
            commandSender, command, label, *arguments
        )

        if (command.name.equals("sethome", true)) return handleSetHomeCommand()

        if (command.name.equals("deletehome", true)) return handleDeleteHomeCommand(commandSender, *arguments)

        return handleHomeCommand(commandSender, *arguments)
    }

    private fun handleHomeCommand(commandSender: User, vararg arguments: String): List<String>? {
        if (!hasCommandPermission(commandSender, "Home.Use", false)) return null

        return handleHomeCompletion(commandSender, *arguments)
    }

    private fun handleSetHomeCommand(): List<String> = listOf()

    private fun handleDeleteHomeCommand(commandSender: User, vararg arguments: String): List<String>? {
        if (!hasCommandPermission(commandSender, "DeleteHome.Use", false)) return null

        return handleHomeCompletion(commandSender, *arguments)
    }

    private fun handleHomeCompletion(commandSender: User, vararg arguments: String): List<String> {
        if (arguments.size != 1) return listOf()

        val homeManager = commandSender.homeManager
        val potentialCompletions = homeManager.homes.map(Home::displayName).toList()

        return potentialCompletions.filter { completion -> completion.startsWith(arguments[0], true) }
    }
}
