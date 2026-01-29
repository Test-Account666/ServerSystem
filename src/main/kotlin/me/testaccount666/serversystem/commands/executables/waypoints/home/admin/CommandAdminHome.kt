package me.testaccount666.serversystem.commands.executables.waypoints.home.admin

import me.testaccount666.serversystem.commands.executables.waypoints.CommandType
import me.testaccount666.serversystem.commands.executables.waypoints.home.AbstractCommandHome
import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.userdata.home.HomeManager
import org.bukkit.command.Command
import org.bukkit.entity.Player

@Suppress("MoveVariableDeclarationIntoWhen")
class CommandAdminHome : AbstractCommandHome() {
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

    override fun getCommandType(command: Command): CommandType {
        val name = command.name.drop("admin".length)

        return when (name) {
            "sethome" -> CommandType.CREATE
            "deletehome" -> CommandType.DELETE
            "home" -> CommandType.TELEPORT
            else -> error("Invalid command name: ${command.name}")
        }
    }

    override fun getPrefix(command: Command): String {
        return when (getCommandType(command)) {
            CommandType.CREATE -> "SetHome"
            CommandType.DELETE -> "DeleteHome"
            CommandType.TELEPORT -> "Home"
        }
    }

    override fun getSyntaxPath(command: Command?) = "AdminHome"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return PermissionManager.hasCommandPermission(player, getBasePermission(command), false)
    }

    override fun canAddPoints(pointManager: HomeManager, command: Command) = true
}
