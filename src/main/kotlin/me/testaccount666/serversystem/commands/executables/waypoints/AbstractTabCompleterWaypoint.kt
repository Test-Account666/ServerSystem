package me.testaccount666.serversystem.commands.executables.waypoints

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.commands.interfaces.getService
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.command.Command

abstract class AbstractTabCompleterWaypoint<T : WaypointManager<P>, P : Waypoint> : ServerSystemTabCompleter {

    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        if (!hasCommandPermission(commandSender, getBasePermission(command), false)) return listOf()
        if (commandSender is ConsoleUser) return listOf()

        val argsBefore = argsBeforePoint(command)
        if (arguments.size <= argsBefore) return null

        if (arguments.size > argsBefore + 1) return listOf()

        val target = fetchTarget(commandSender, command, *arguments) ?: return listOf()
        val waypointManager = getWaypointManager(command, target)

        return waypointManager.waypoints
            .filter { it.name.startsWith(arguments[argsBefore], true) }
            .map { it.name }
    }

    private fun fetchTarget(commandSender: User, command: Command, vararg arguments: String): User? {
        if (argsBeforePoint(command) == 0) return commandSender

        val index = argsBeforePoint(command) - 1
        return getTargetUser(index, *arguments)
    }

    abstract fun getBasePermission(command: Command): String

    abstract fun argsBeforePoint(command: Command): Int

    abstract fun getWaypointManager(command: Command, targetUser: User): T

    private fun getTargetUser(index: Int = 0, vararg arguments: String): User? {
        if (arguments.size <= index) return null

        val registry = getService<UserManager>()
        val cachedUser = registry.getUserOrNull(arguments[index], true)
        return cachedUser?.offlineUser as? User
    }
}
