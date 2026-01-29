package me.testaccount666.serversystem.commands.executables.waypoints.warp

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.waypoints.AbstractCommandWaypoint
import me.testaccount666.serversystem.commands.executables.waypoints.CommandType
import me.testaccount666.serversystem.commands.executables.waypoints.warp.manager.Warp
import me.testaccount666.serversystem.commands.executables.waypoints.warp.manager.WarpManager
import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("warp", ["setwarp", "deletewarp"], TabCompleterWarp::class)
class CommandWarp : AbstractCommandWaypoint<WarpManager, Warp>() {
    override fun argsBeforePoint(command: Command) = 0

    override fun getPlaceholder() = "<WARP>"

    override fun canAddPoints(pointManager: WarpManager, command: Command) = true

    override fun getWaypointManager(command: Command, targetUser: User) = getService<WarpManager>()

    override fun createPoint(name: String, location: Location) = Warp.of(name, location)

    override fun getBasePermission(command: Command): String {
        return when (getCommandType(command)) {
            CommandType.CREATE -> "Warp.Set"
            CommandType.DELETE -> "Warp.Delete"
            CommandType.TELEPORT -> "Warp.Teleport"
        }
    }

    override fun getCommandType(command: Command): CommandType {
        return when (command.name.lowercase()) {
            "setwarp" -> CommandType.CREATE
            "deletewarp" -> CommandType.DELETE
            "warp" -> CommandType.TELEPORT
            else -> error("Invalid command name: ${command.name}")
        }
    }

    override fun getPrefix(command: Command): String {
        return when (getCommandType(command)) {
            CommandType.CREATE -> "Warp.Set"
            CommandType.DELETE -> "Warp.Delete"
            CommandType.TELEPORT -> "Warp.Use"
        }
    }

    override fun getSyntaxPath(command: Command?) = "Warp"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return PermissionManager.hasCommandPermission(player, getBasePermission(command), false)
    }
}
