package me.testaccount666.serversystem.commands.executables.waypoints.home.user

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.waypoints.CommandType
import me.testaccount666.serversystem.commands.executables.waypoints.home.AbstractCommandHome
import me.testaccount666.serversystem.commands.executables.waypoints.home.admin.CommandAdminHome
import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.userdata.home.HomeManager
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

    override fun argsBeforePoint(command: Command): Int {
        if (isAdminCommand(command)) return _commandAdminHome.argsBeforePoint(command)
        return 0
    }

    override fun getCommandType(command: Command): CommandType {
        if (isAdminCommand(command)) return _commandAdminHome.getCommandType(command)
        return when (command.name) {
            "sethome" -> CommandType.CREATE
            "deletehome" -> CommandType.DELETE
            "home" -> CommandType.TELEPORT
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

    override fun canAddPoints(pointManager: HomeManager, command: Command): Boolean {
        if (isAdminCommand(command)) return _commandAdminHome.canAddPoints(pointManager, command)
        return pointManager.canAddPoints()
    }

    override fun getPrefix(command: Command): String {
        return when (getCommandType(command)) {
            CommandType.CREATE -> "SetHome"
            CommandType.DELETE -> "DeleteHome"
            CommandType.TELEPORT -> "Home"
        }
    }

    private fun isAdminCommand(command: Command?) = command?.name?.startsWith("admin", true) ?: false
}
