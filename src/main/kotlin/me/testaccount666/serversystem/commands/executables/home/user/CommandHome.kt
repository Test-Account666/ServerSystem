package me.testaccount666.serversystem.commands.executables.home.user

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.home.AbstractCommandHome
import me.testaccount666.serversystem.commands.executables.home.HomeType
import me.testaccount666.serversystem.commands.executables.home.admin.CommandAdminHome
import me.testaccount666.serversystem.managers.PermissionManager
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand(
    "home",
    ["sethome", "deletehome", "adminhome", "adminsethome", "admindeletehome"],
    TabCompleterHome::class
)
class CommandHome : AbstractCommandHome() {
    private val _commandAdminHome = CommandAdminHome()

    override fun getBasePermission(command: Command): String {
        if (isAdminCommand(command)) return _commandAdminHome.getBasePermission(command)
        return when (command.name) {
            "sethome" -> "Home.Set"
            "deletehome" -> "Home.Delete"
            "home" -> "Home.Use"
            else -> error("Invalid command name: ${command.name}")
        }
    }

    override fun argsBeforeHome(command: Command): Int {
        if (isAdminCommand(command)) return _commandAdminHome.argsBeforeHome(command)
        return 0
    }

    override fun getHomeType(command: Command): HomeType {
        if (isAdminCommand(command)) return _commandAdminHome.getHomeType(command)
        return when (command.name) {
            "sethome" -> HomeType.SET
            "deletehome" -> HomeType.DELETE
            "home" -> HomeType.TELEPORT
            else -> error("Invalid command name: ${command.name}")
        }
    }

    override fun getSyntaxPath(command: Command?): String {
        if (isAdminCommand(command)) return _commandAdminHome.getSyntaxPath(command)
        return "Home"
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        if (isAdminCommand(command)) return _commandAdminHome.hasCommandAccess(player, command)
        return PermissionManager.hasCommandPermission(player, getBasePermission(command), false)
    }

    private fun isAdminCommand(command: Command?) = command?.name?.startsWith("admin", ignoreCase = true) ?: false
}
