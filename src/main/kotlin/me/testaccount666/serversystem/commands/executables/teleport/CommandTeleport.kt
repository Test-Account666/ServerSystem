package me.testaccount666.serversystem.commands.executables.teleport

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.text.DecimalFormat
import java.util.*
import java.util.function.Consumer

@ServerSystemCommand("teleport", ["teleportposition", "teleporthere", "teleportall"])
class CommandTeleport : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val commandName = command.name.lowercase(Locale.getDefault())
        when (commandName) {
            "teleportposition" -> executeTeleportPosition(commandSender, command, label, *arguments)
            "teleporthere" -> executeTeleportHere(commandSender, command, label, *arguments)
            "teleportall" -> executeTeleportAll(commandSender)
            else -> {
                if (arguments.size == 2) executeTeleportOther(commandSender, command, label, *arguments)
                else if (arguments.size > 2) executeTeleportPosition(commandSender, command, label, *arguments)
                else executeTeleport(commandSender, command, label, *arguments)
            }
        }
    }

    private fun executeTeleportAll(commandSender: User) {
        if (!validateSenderAndPermission(commandSender, "TeleportAll.Use")) return

        val senderLocation = commandSender.getPlayer()!!.location
        Bukkit.getOnlinePlayers().forEach { player -> player.teleport(senderLocation) }
        command("TeleportAll.Success", commandSender) { target("*") }.build()
    }

    private fun executeTeleportHere(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!validateSenderAndPermission(commandSender, "TeleportHere.Use")) return

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        getTargetUserAndTeleport(
            commandSender,
            { targetPlayer -> targetPlayer.teleport(commandSender.getPlayer()!!.location) },
            "TeleportHere.Success", *arguments
        )
    }

    private fun executeTeleport(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!validateSenderAndPermission(commandSender, "Teleport.Use")) return

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        getTargetUserAndTeleport(
            commandSender,
            { targetPlayer: Player? -> commandSender.getPlayer()!!.teleport(targetPlayer!!.location) },
            "Teleport.Success", *arguments
        )
    }

    private fun executeTeleportOther(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!validatePermissions(commandSender, "Teleport.Use", "Teleport.Other")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, 1, *arguments)) return

        val sourceUser = getTargetUser(commandSender, arguments = arguments)
        val targetUser = getTargetUser(commandSender, 1, false, *arguments)

        if (sourceUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[1]) }.build()
            return
        }

        val sourcePlayer = sourceUser.getPlayer()
        val targetPlayer = targetUser.getPlayer()

        sourcePlayer!!.teleport(targetPlayer!!.location)
        command("Teleport.SuccessOther", commandSender) {
            target(sourcePlayer.name)
            postModifier { it.replace("<TARGET2>", targetPlayer.name) }
        }.build()
    }

    private fun executeTeleportPosition(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!validatePermissions(commandSender, "TeleportPosition.Use")) return
        if (arguments.size < 3) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        var isSelf = true
        var startIndex = 0

        if (arguments.size > 3) {
            val potentialTargetUser = getTargetUser(commandSender, arguments = arguments)
            if (potentialTargetUser != null) {
                isSelf = false
                startIndex = 1
            }
        }

        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, 3, *arguments)) return

        val targetUser = if (isSelf) commandSender else getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!

        if (!isSelf && !checkOtherPermission(commandSender, "TeleportPosition.Other", targetPlayer.name)) return

        val executionLocation = if (commandSender is ConsoleUser) targetPlayer.location else commandSender.getPlayer()!!.location

        val location = extractLocationWithRotation(executionLocation, targetPlayer, startIndex, *arguments)

        if (location == null) {
            command("TeleportPosition.InvalidLocation", commandSender) { target(targetPlayer.name) }.build()
            return
        }

        if (!isValidTeleportLocation(location, commandSender, targetPlayer)) return

        targetPlayer.teleport(location)
        sendTeleportPositionSuccess(commandSender, targetPlayer, location, isSelf)
    }

    private fun isValidTeleportLocation(location: Location, commandSender: User, targetPlayer: Player): Boolean {
        if (!location.world.worldBorder.isInside(location)) {
            command("TeleportPosition.OutsideBorder", commandSender) { target(targetPlayer.name) }.build()
            return false
        }

        return location.world === targetPlayer.world || checkBasePermission(commandSender, "TeleportPosition.World")
    }

    private fun sendTeleportPositionSuccess(commandSender: User, targetPlayer: Player, location: Location, isSelf: Boolean) {
        val messagePath = if (isSelf) "TeleportPosition.Success" else "TeleportPosition.SuccessOther"
        command(messagePath, commandSender) {
            target(targetPlayer.name)
            postModifier { formatLocationMessage(it, location) }
        }.build()
    }

    private fun formatLocationMessage(message: String, location: Location): String {
        return message.replace("<X>", roundDecimal(location.x))
            .replace("<Y>", roundDecimal(location.y))
            .replace("<Z>", roundDecimal(location.z))
            .replace("<WORLD>", location.world.name)
    }

    private fun validateSenderAndPermission(commandSender: User, permission: String): Boolean {
        if (!checkBasePermission(commandSender, permission)) return false
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return false
        }
        return true
    }

    private fun validatePermissions(commandSender: User, vararg permissions: String): Boolean {
        for (permission in permissions) if (!checkBasePermission(commandSender, permission)) return false
        return true
    }

    private fun getTargetUserAndTeleport(commandSender: User, teleportAction: Consumer<Player>, successMessage: String, vararg arguments: String) {
        val targetUser = getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        teleportAction.accept(targetPlayer)
        command(successMessage, commandSender) { target(targetPlayer.name) }.build()
    }


    private fun roundDecimal(location: Double): String {
        val format = DecimalFormat("0.##")

        return format.format(location).replace(",", ".")
    }

    fun extractLocationWithRotation(executeLocation: Location, target: Entity, startIndex: Int, vararg arguments: String): Location? {
        val coordinateEndIndex = startIndex + 3

        if (arguments.size < coordinateEndIndex) return null

        val x = calculateRelativePosition(_X_AXIS, arguments[startIndex], executeLocation, target.location)
        val y = calculateRelativePosition(_Y_AXIS, arguments[startIndex + 1], executeLocation, target.location)
        val z = calculateRelativePosition(_Z_AXIS, arguments[startIndex + 2], executeLocation, target.location)

        if (x == null || y == null || z == null) return null

        var yaw = target.location.yaw
        var pitch = target.location.pitch

        var currentIndex = coordinateEndIndex

        if (arguments.size > currentIndex) {
            val yawVal = calculateRelativeRotation(arguments[currentIndex], executeLocation, target.location, true)
            if (yawVal != null) {
                yaw = yawVal
                currentIndex++
            }
            // If parsing fails, it might be a world name, so we skip yaw parsing
        }

        if (arguments.size > currentIndex) {
            val pitchVal = calculateRelativeRotation(arguments[currentIndex], executeLocation, target.location, false)
            if (pitchVal != null) {
                pitch = pitchVal
                currentIndex++
            }
            // If parsing fails, it might be a world name, so we skip pitch parsing
        }

        var world: World? = target.world
        if (arguments.size > currentIndex) {
            val worldName = arguments[currentIndex]
            val targetWorld = target.server.getWorld(worldName)
            if (targetWorld != null) world = targetWorld
        }

        return Location(world, x, y, z, yaw, pitch)
    }

    private fun calculateRelativePosition(axis: Vector, input: String, senderLocation: Location, targetLocation: Location): Double? {
        try {
            if (input == "~") return getCoordinate(targetLocation, axis)
            if (input == "@") return getCoordinate(senderLocation, axis)

            if (input.startsWith("~")) return parseOffset(input, targetLocation, axis, 1)
            if (input.startsWith("@")) return parseOffset(input, senderLocation, axis, 1)

            return input.toDouble()
        } catch (_: NumberFormatException) {
            return null
        }
    }

    private fun calculateRelativeRotation(input: String, senderLocation: Location, targetLocation: Location, isYaw: Boolean): Float? {
        try {
            if (input == "~") return if (isYaw) targetLocation.yaw else targetLocation.pitch
            if (input == "@") return if (isYaw) senderLocation.yaw else senderLocation.pitch

            if (input.startsWith("~")) return parseRotationOffset(input, targetLocation, isYaw, 1)
            if (input.startsWith("@")) return parseRotationOffset(input, senderLocation, isYaw, 1)

            return input.toFloat()
        } catch (_: NumberFormatException) {
            return null
        }
    }

    private fun parseRotationOffset(input: String, location: Location, isYaw: Boolean, offsetStartIndex: Int): Float {
        val offset = input.substring(offsetStartIndex).toFloat()
        val currentValue = if (isYaw) location.yaw else location.pitch
        return currentValue + offset
    }

    private fun parseOffset(input: String, location: Location, axis: Vector, offsetStartIndex: Int): Double {
        val offset = input.substring(offsetStartIndex).toDouble()
        return getCoordinate(location, axis) + offset
    }

    private fun getCoordinate(location: Location, axis: Vector): Double {
        return (axis.getX() * location.x + axis.getY() * location.y + axis.getZ() * location.z)
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "Teleport"

        val commandName = command.name.lowercase(Locale.getDefault())
        return when (commandName) {
            "teleportposition" -> "TeleportPosition"
            "teleporthere" -> "TeleportHere"
            "teleportall" -> "TeleportAll"
            else -> "Teleport"
        }
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        val commandName = command.name.lowercase(Locale.getDefault())
        val permission = when (commandName) {
            "teleportposition" -> "TeleportPosition.Use"
            "teleporthere" -> "TeleportHere.Use"
            "teleportall" -> "TeleportAll.Use"
            else -> "Teleport.Use"
        }
        return hasCommandPermission(player, permission, false)
    }

    companion object {
        private val _X_AXIS = Vector(1, 0, 0)
        private val _Y_AXIS = Vector(0, 1, 0)
        private val _Z_AXIS = Vector(0, 0, 1)
    }
}