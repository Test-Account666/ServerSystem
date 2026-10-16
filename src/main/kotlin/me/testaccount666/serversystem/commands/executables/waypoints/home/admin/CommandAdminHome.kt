package me.testaccount666.serversystem.commands.executables.waypoints.home.admin

import me.testaccount666.serversystem.commands.executables.waypoints.CommandType
import me.testaccount666.serversystem.commands.executables.waypoints.home.AbstractCommandHome
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.home.HomeManager
import org.bukkit.command.Command

@Suppress("MoveVariableDeclarationIntoWhen")
class CommandAdminHome : AbstractCommandHome() {
    override fun minRequiredArguments(command: Command) = argsBeforePoint(command) + 1
    override fun getUsagePermission(command: Command): String {
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
    override fun canAddPoints(pointManager: HomeManager, command: Command) = true
    override fun canInstantTeleport(command: Command, user: User) = true
}
