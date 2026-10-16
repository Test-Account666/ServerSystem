package me.testaccount666.serversystem.commands.executables.teleport

import me.testaccount666.paperktx.extensions.location
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

@ServerSystemCommand("teleport", ["teleportposition", "teleporthere", "teleportall"])
class CommandTeleport : AbstractServerSystemCommand() {
    private val commandHandlers = mapOf(
        "teleportposition" to TeleportHandler.TeleportPosition(),
        "teleporthere" to TeleportHandler.Teleport(),
        "teleport" to TeleportHandler.Teleport(),
        "teleportall" to TeleportHandler.TeleportAll()
    )

    override fun minRequiredArguments(command: Command): Int {
        return commandHandlers[command.name.lowercase()]?.requiredArguments() ?: error("(CommandTeleport) Unexpected value: ${command.name}")
    }

    override fun getUsagePermission(command: Command): String {
        return commandHandlers[command.name.lowercase()]?.usagePermission() ?: error("(CommandTeleport) Unexpected value: ${command.name}")
    }

    override fun getSyntaxPath(command: Command?): String {
        command ?: return "Teleport"

        return commandHandlers[command.name.lowercase()]?.usageSyntax() ?: error("(CommandTeleport) Unexpected value: ${command.name}")
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val commandName = command.name.lowercase().let {
            if (it == "teleport" && arguments.size >= 3) "teleportposition" else it
        }

        val handler = commandHandlers[commandName] ?: return
        if (!checkPermission(commandSender, handler.usagePermission())) return

        if (arguments.size < handler.requiredArguments()) {
            commandSender.generalMsg("InvalidArguments") {
                syntax(getSyntaxPath(command))
                label(label)
            }
            return
        }

        when (handler) {
            is TeleportHandler.Teleport -> executeTeleport(handler, commandSender, command, label, *arguments)
            is TeleportHandler.TeleportPosition -> executeTeleportPosition(handler, commandSender, command, label, *arguments)
            is TeleportHandler.TeleportAll -> {
                if (!isPlayer(commandSender)) return
                handler.execute(commandSender, label)
            }
        }
    }

    fun executeTeleportPosition(handler: TeleportHandler.TeleportPosition, commandSender: User, command: Command, label: String, vararg arguments: String) {
        var isSelf = true
        var startIndex = 0
        var targetUser = commandSender

        if (arguments.size > 3) {
            getTargetUser(commandSender, arguments = arguments)?.also {
                isSelf = false
                startIndex = 1
                targetUser = it
            }
        }

        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, 3, *arguments)) return

        val targetPlayer = targetUser.getPlayer()!!
        if (!isSelf && !checkPermission(commandSender, "TeleportPosition.Other", targetPlayer.name)) return

        val executionLocation = if (commandSender is ConsoleUser) targetPlayer.location else commandSender.getPlayer()!!.location

        val location = loc(executionLocation, targetPlayer, startIndex, *arguments) ?: run {
            commandSender.commandMsg("TeleportPosition.InvalidLocation") { target(targetPlayer.name) }
            return
        }

        if (!isValidTeleportLocation(location, commandSender, targetPlayer)) return

        handler.setup(targetUser, location)
        handler.execute(commandSender, label)
    }

    fun executeTeleport(handler: TeleportHandler.Teleport, commandSender: User, command: Command, label: String, vararg arguments: String) {
        val isSelf = arguments.size < 2
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, 1, *arguments)) return

        val targetUser = (if (isSelf) commandSender else getTargetUser(commandSender, arguments = arguments)) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        val toTarget = getTargetUser(commandSender, 1, false, *arguments)

        if (toTarget == null) handler.setup(commandSender, targetUser)
        else handler.setup(targetUser, toTarget)

        handler.execute(commandSender, label)
    }

    private fun isValidTeleportLocation(location: Location, commandSender: User, targetPlayer: Player): Boolean {
        if (!location.world.worldBorder.isInside(location)) {
            commandSender.commandMsg("TeleportPosition.OutsideBorder") { target(targetPlayer.name) }
            return false
        }

        return location.world === targetPlayer.world || checkPermission(commandSender, "TeleportPosition.World")
    }

    fun loc(executeLocation: Location, target: Entity, startIndex: Int, vararg arguments: String): Location? {
        val coordinateEndIndex = startIndex + 3

        if (arguments.size < coordinateEndIndex) return null

        val x = extractCoordinate(executeLocation.x, target.location.x, arguments[startIndex]) ?: return null
        val y = extractCoordinate(executeLocation.y, target.location.y, arguments[startIndex + 1]) ?: return null
        val z = extractCoordinate(executeLocation.z, target.location.z, arguments[startIndex + 2]) ?: return null

        var yaw = target.location.yaw
        var pitch = target.location.pitch

        var currentIndex = coordinateEndIndex

        if (arguments.size > currentIndex) {
            val yawVal = extractCoordinate(executeLocation.yaw.toDouble(), yaw.toDouble(), arguments[currentIndex])
            if (yawVal != null) {
                yaw = yawVal.toFloat()
                currentIndex++
            }
            // If parsing fails, it might be a world name, so we skip yaw parsing
        }

        if (arguments.size > currentIndex) {
            val pitchVal = extractCoordinate(executeLocation.pitch.toDouble(), pitch.toDouble(), arguments[currentIndex])
            if (pitchVal != null) {
                pitch = pitchVal.toFloat()
                currentIndex++
            }
            // If parsing fails, it might be a world name, so we skip pitch parsing
        }

        var world = target.world
        if (arguments.size > currentIndex) {
            val worldName = arguments[currentIndex]
            target.server.getWorld(worldName)?.let { world = it }
        }

        return location(x, y, z, world, yaw, pitch)
    }

    private fun extractCoordinate(origin: Double, altOrigin: Double, token: String): Double? {
        return when {
            token == "~" -> altOrigin
            token == "@" -> origin
            token.startsWith("~") -> {
                val offset = token.drop(1).toDoubleOrNull() ?: return null
                altOrigin + offset
            }

            token.startsWith("@") -> {
                val offset = token.drop(1).toDoubleOrNull() ?: return null
                origin + offset
            }

            else -> token.toDoubleOrNull()
        }
    }

    sealed class TeleportHandler {
        abstract fun requiredArguments(): Int
        abstract fun usagePermission(): String
        abstract fun usageSyntax(): String
        abstract fun execute(commandSender: User, label: String)

        class Teleport : TeleportHandler() {
            override fun requiredArguments() = 1
            override fun usagePermission() = "Teleport.Use"
            override fun usageSyntax() = "Teleport"

            private lateinit var _target: User
            private lateinit var _toTarget: User

            fun setup(target: User, toTarget: User) {
                _target = target
                _toTarget = toTarget
            }

            override fun execute(commandSender: User, label: String) {
                _target.getPlayer()!!.teleportAsync(_toTarget.getPlayer()!!.location)

                val isSelf = commandSender === _target
                val successMessage = if (isSelf) "Teleport.Success" else "Teleport.SuccessOther"
                commandSender.commandMsg(successMessage) {
                    target(_target.nameSafe)
                    postModifier { it.replace("<TARGET2>", _toTarget.nameSafe) }
                }
            }
        }

        class TeleportPosition : TeleportHandler() {
            override fun requiredArguments() = 3
            override fun usagePermission() = "TeleportPosition.Use"
            override fun usageSyntax() = "TeleportPosition"

            private lateinit var _target: User
            private lateinit var _location: Location

            fun setup(target: User, location: Location) {
                _target = target
                _location = location
            }

            override fun execute(commandSender: User, label: String) {
                val isSelf = commandSender === _target
                val successMessage = if (isSelf) "TeleportPosition.Success" else "TeleportPosition.SuccessOther"
                _target.getPlayer()!!.teleportAsync(_location)

                commandSender.commandMsg(successMessage) {
                    target(_target.nameSafe)
                    postModifier { formatLocationMessage(it, _location) }
                }
            }

            private fun formatLocationMessage(message: String, location: Location): String {
                return message.replace("<X>", location.x.format())
                    .replace("<Y>", location.y.format())
                    .replace("<Z>", location.z.format())
                    .replace("<WORLD>", location.world.name)
            }
        }

        class TeleportAll : TeleportHandler() {
            override fun requiredArguments() = 0
            override fun usagePermission() = "TeleportAll.Use"
            override fun usageSyntax() = "TeleportAll"

            override fun execute(commandSender: User, label: String) {
                Bukkit.getOnlinePlayers().forEach { it.teleportAsync(commandSender.getPlayer()!!.location) }
                commandSender.commandMsg("TeleportAll.Success") { target("*") }
            }
        }
    }
}