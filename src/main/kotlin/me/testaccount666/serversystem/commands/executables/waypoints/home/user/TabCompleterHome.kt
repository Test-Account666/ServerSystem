package me.testaccount666.serversystem.commands.executables.waypoints.home.user

import me.testaccount666.serversystem.commands.executables.waypoints.home.AbstractTabCompleterHome
import me.testaccount666.serversystem.commands.executables.waypoints.home.admin.TabCompleterAdminHome
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class TabCompleterHome : AbstractTabCompleterHome() {
    private val _tabCompleterAdminHome = TabCompleterAdminHome()

    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        if (isAdminCommand(command)) return _tabCompleterAdminHome.tabComplete(commandSender, command, label, *arguments)

        return super.tabComplete(commandSender, command, label, *arguments)
    }

    override fun getBasePermission(command: Command): String {
        if (isAdminCommand(command)) return _tabCompleterAdminHome.getBasePermission(command)
        return when (command.name) {
            "sethome" -> "Home.Set"
            "deletehome" -> "Home.Delete"
            "home" -> "Home.Use"
            else -> error("Invalid command name: ${command.name}")
        }
    }

    override fun argsBeforePoint(command: Command): Int {
        if (isAdminCommand(command)) return _tabCompleterAdminHome.argsBeforePoint(command)
        return 0
    }

    private fun isAdminCommand(command: Command?) = command?.name?.startsWith("admin", true) ?: false
}
