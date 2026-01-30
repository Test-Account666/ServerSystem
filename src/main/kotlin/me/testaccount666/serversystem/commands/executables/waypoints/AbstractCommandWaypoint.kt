package me.testaccount666.serversystem.commands.executables.waypoints

import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import me.testaccount666.serversystem.utils.tuples.Tuple
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
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
            command("${getPrefix(command)}.AlreadyExists", commandSender) {
                target(target.getNameSafe())
                postModifier { it.replace(getPlaceholder(), pointName) }
            }.build()
            return
        }

        val location = commandSender.getPlayer()!!.location
        val point = createPoint(pointName, location) ?: run {
            command("${getPrefix(command)}.InvalidName", commandSender) {
                target(target.getNameSafe())
            }.build()
            return
        }

        if (!canAddPoints(pointManager, command)) {
            command("${getPrefix(command)}.MaxReached", commandSender) { target(target.getNameSafe()) }.build()
            return
        }

        pointManager.addPoint(point)

        command("${getPrefix(command)}.Success", commandSender) {
            target(target.getNameOrNull())
            postModifier { it.replace(getPlaceholder(), point.displayName) }
        }.build()
    }

    protected fun handleDelete(commandSender: User, command: Command, target: User, pointManager: T, pointName: String) {
        val point = pointManager.getPointByName(pointName) ?: run {
            sendNotExistMessage(commandSender, target, command, pointName)
            return
        }

        pointManager.removePoint(point)

        command("${getPrefix(command)}.Success", commandSender) {
            target(target.getNameSafe())
            postModifier { it.replace(getPlaceholder(), point.displayName) }
        }.build()
    }

    protected fun handleTeleport(commandSender: User, command: Command, target: User, pointManager: T, pointName: String) {
        val point = pointManager.getPointByName(pointName) ?: run {
            sendNotExistMessage(commandSender, target, command, pointName)
            return
        }

        val pointLocation = point.location
        val player = commandSender.getPlayer()!!

        playAnimation(player.location)
        player.teleport(pointLocation)
        playAnimation(pointLocation)

        command("${getPrefix(command)}.Success", commandSender) {
            target(target.getNameSafe())
            postModifier { it.replace(getPlaceholder(), point.displayName) }
        }.build()
    }

    private fun resolveTargetAndPoint(commandSender: User, command: Command, vararg arguments: String): Tuple<User, String>? {
        val target = fetchTarget(commandSender, command, *arguments) ?: return null

        val pointName = arguments[argsBeforePoint(command)]
        return Tuple(target, pointName)
    }

    private fun fetchTarget(commandSender: User, command: Command, vararg arguments: String): User? {
        if (argsBeforePoint(command) == 0) return commandSender

        val index = argsBeforePoint(command) - 1

        val target = getTargetUser(commandSender, index, false, *arguments) ?: run {
            general("PlayerNotFound", commandSender) { target(arguments[index]) }.build()
            return null
        }

        return target
    }

    private fun sendNotExistMessage(commandSender: User, target: User, command: Command, pointName: String) {
        command("${getPrefix(command)}.DoesNotExist", commandSender) {
            target(target.getNameSafe())
            postModifier { it.replace(getPlaceholder(), pointName) }
        }.build()
    }

    private fun playAnimation(location: Location) {
        location.world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
        location.world.spawnParticle(Particle.PORTAL, location, 100, 0.5, 0.5, 0.5, 0.05)
    }

    abstract fun argsBeforePoint(command: Command): Int
    abstract fun getWaypointManager(command: Command, targetUser: User): T
    abstract fun getCommandType(command: Command): CommandType
    abstract fun canAddPoints(pointManager: T, command: Command): Boolean
    abstract fun createPoint(name: String, location: Location): P?

    abstract fun getPrefix(command: Command): String
    abstract fun getPlaceholder(): String
}

enum class CommandType { CREATE, DELETE, TELEPORT }