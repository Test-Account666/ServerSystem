package me.testaccount666.serversystem.commands.executables.waypoints.home.admin

import me.testaccount666.serversystem.commands.executables.waypoints.home.AbstractTabCompleterHome
import org.bukkit.command.Command

class TabCompleterAdminHome : AbstractTabCompleterHome() {
    override fun getBasePermission(command: Command): String {
        val name = command.name.drop("admin".length)

        return when (name) {
            "sethome" -> "AdminHome.Set"
            "deletehome" -> "AdminHome.Delete"
            "home" -> "AdminHome.Use"
            else -> error("Invalid command name: ${command.name}")
        }
    }

    override fun argsBeforePoint(command: Command) = 1
}
