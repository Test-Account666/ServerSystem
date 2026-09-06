package me.testaccount666.serversystem.commands.executables.waypoints

import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.teleport.TeleportRunnable.Companion.teleportSmart
import org.bukkit.Location
import org.bukkit.command.Command

abstract class AbstractCommandWaypoint<T : WaypointManager<P>, P : Waypoint> : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        val (target, pointName) = resolveTargetAndPoint(commandSender, command, *arguments) ?: return
        val pointManager = getWaypointManager(command, target)

        when (getCommandType(command)) {
            CommandType.CREATE -> handleSet(commandSender, command, target, pointManager, pointName)
            CommandType.DELETE -> handleDelete(commandSender, command, target, pointManager, pointName)
            CommandType.TELEPORT -> handleTeleport(commandSender, command, target, pointManager, pointName)
        }
    }

    protected fun handleSet(commandSender: User, command: Command, target: User, pointManager: T, pointName: String) {
        if (pointManager.pointExists(pointName)) {
            commandSender.commandMsg("${getPrefix(command)}.AlreadyExists") {
                target(target.nameSafe)
                postModifier { it.replace(getPlaceholder(), pointName) }
            }
            return
        }

        val location = commandSender.getPlayer()!!.location
        val point = createPoint(pointName, location) ?: run {
            commandSender.commandMsg("${getPrefix(command)}.InvalidName") {
                target(target.nameSafe)
            }
            return
        }

        if (!canAddPoints(pointManager, command)) {
            commandSender.commandMsg("${getPrefix(command)}.MaxReached") { target(target.nameSafe) }
            return
        }

        pointManager.addPoint(point)

        commandSender.commandMsg("${getPrefix(command)}.Success") {
            target(target.getNameOrNull())
            postModifier { it.replace(getPlaceholder(), point.displayName) }
        }
    }

    protected fun handleDelete(commandSender: User, command: Command, target: User, pointManager: T, pointName: String) {
        val point = pointManager.getPointByName(pointName) ?: run {
            sendNotExistMessage(commandSender, target, command, pointName)
            return
        }

        pointManager.removePoint(point)

        commandSender.commandMsg("${getPrefix(command)}.Success") {
            target(target.nameSafe)
            postModifier { it.replace(getPlaceholder(), point.displayName) }
        }
    }

    protected fun handleTeleport(commandSender: User, command: Command, target: User, pointManager: T, pointName: String) {
        val point = pointManager.getPointByName(pointName) ?: run {
            sendNotExistMessage(commandSender, target, command, pointName)
            return
        }

        commandSender.teleportSmart(
            point.location,
            canInstantTeleport(command, commandSender),
            { commandSender.commandMsg("${getPrefix(command)}.Teleporting") { target(target.nameSafe) } },
            {
                commandSender.commandMsg("${getPrefix(command)}.Success") {
                    target(target.nameSafe)
                    postModifier { it.replace(getPlaceholder(), point.displayName) }
                }
            },
            { commandSender.commandMsg("${getPrefix(command)}.Moved") { target(target.nameSafe) } }
        )
    }

    private fun resolveTargetAndPoint(commandSender: User, command: Command, vararg arguments: String): Pair<User, String>? {
        val target = fetchTarget(commandSender, command, *arguments) ?: return null

        val pointName = arguments[argsBeforePoint(command)]
        return Pair(target, pointName)
    }

    private fun fetchTarget(commandSender: User, command: Command, vararg arguments: String): User? {
        if (argsBeforePoint(command) == 0) return commandSender

        val index = argsBeforePoint(command) - 1

        val target = getTargetUser(commandSender, index, false, *arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[index]) }
            return null
        }

        return target
    }

    private fun sendNotExistMessage(commandSender: User, target: User, command: Command, pointName: String) {
        commandSender.commandMsg("${getPrefix(command)}.DoesNotExist") {
            target(target.nameSafe)
            postModifier { it.replace(getPlaceholder(), pointName) }
        }
    }

    abstract fun argsBeforePoint(command: Command): Int
    abstract fun getWaypointManager(command: Command, targetUser: User): T
    abstract fun getCommandType(command: Command): CommandType
    abstract fun canAddPoints(pointManager: T, command: Command): Boolean
    abstract fun createPoint(name: String, location: Location): P?

    abstract fun getPrefix(command: Command): String
    abstract fun getPlaceholder(): String
    abstract fun canInstantTeleport(command: Command, user: User): Boolean
}

enum class CommandType { CREATE, DELETE, TELEPORT }