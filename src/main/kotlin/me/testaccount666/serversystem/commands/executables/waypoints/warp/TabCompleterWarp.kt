package me.testaccount666.serversystem.commands.executables.waypoints.warp

import me.testaccount666.serversystem.commands.executables.waypoints.AbstractTabCompleterWaypoint
import me.testaccount666.serversystem.commands.executables.waypoints.warp.manager.Warp
import me.testaccount666.serversystem.commands.executables.waypoints.warp.manager.WarpManager
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class TabCompleterWarp : AbstractTabCompleterWaypoint<WarpManager, Warp>() {
    override fun argsBeforePoint(command: Command) = 0

    override fun getWaypointManager(command: Command, targetUser: User) = getService<WarpManager>()

    override fun getBasePermission(command: Command): String {
        return when (command.name) {
            "setwarp" -> "Warp.Set"
            "deletewarp" -> "Warp.Delete"
            "warp" -> "Warp.Use"
            else -> error("Invalid command name: ${command.name}")
        }
    }
}
