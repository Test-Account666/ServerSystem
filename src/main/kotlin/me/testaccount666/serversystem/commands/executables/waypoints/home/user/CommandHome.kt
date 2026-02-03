package me.testaccount666.serversystem.commands.executables.waypoints.home.user

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.waypoints.CommandType
import me.testaccount666.serversystem.commands.executables.waypoints.home.AbstractCommandHome
import me.testaccount666.serversystem.commands.executables.waypoints.home.admin.CommandAdminHome
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.home.HomeManager
import org.bukkit.command.Command

@ServerSystemCommand(
    "home",
    ["sethome", "deletehome", "adminhome", "adminsethome", "admindeletehome"],
    TabCompleterHome::class
)
class CommandHome : AbstractCommandHome() {
    private val _commandAdminHome = CommandAdminHome()

    override fun minRequiredArguments(command: Command): Int {
        if (isAdminCommand(command)) return _commandAdminHome.minRequiredArguments(command)
        return argsBeforePoint(command) + 1
    }

    override fun getUsagePermission(command: Command): String {
        if (isAdminCommand(command)) return _commandAdminHome.getUsagePermission(command)
        return when (command.name) {
            "sethome" -> "Home.Set"
            "deletehome" -> "Home.Delete"
            "home" -> "Home.Use"
            else -> error("Invalid command name: ${command.name}")
        }
    }

    override fun getSyntaxPath(command: Command?): String {
        if (isAdminCommand(command)) return _commandAdminHome.getSyntaxPath(command)
        return "Home"
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

    override fun canInstantTeleport(command: Command, user: User): Boolean {
        if (isAdminCommand(command)) return _commandAdminHome.canInstantTeleport(command, user)
        return hasCommandPermission(user, "Home.InstantTeleport", false)
    }

    private fun isAdminCommand(command: Command?) = command?.name?.startsWith("admin", true) ?: false
}
