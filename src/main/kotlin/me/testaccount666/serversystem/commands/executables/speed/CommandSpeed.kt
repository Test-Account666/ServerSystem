package me.testaccount666.serversystem.commands.executables.speed

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.max
import kotlin.math.min

@ServerSystemCommand("speed", ["flyspeed", "walkspeed"])
class CommandSpeed : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Speed.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, 1, *arguments)) return

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val targetUser = getTargetUser(commandSender, 1, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[1]) }.build()
            return
        }
        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkOtherPermission(commandSender, "Speed.Other", targetPlayer.name)) return

        val speedType = when (command.name.lowercase(Locale.getDefault())) {
            "flyspeed" -> "Fly"
            "walkspeed" -> "Walk"
            else -> if (targetPlayer.isFlying) "Fly" else "Walk"
        }

        var speed: Float
        try {
            speed = arguments[0].toFloat()
            speed = max(0f, speed)
            speed = min(10f, speed)
        } catch (_: NumberFormatException) {
            command("Speed.InvalidSpeed", commandSender) { target(targetPlayer.name) }.build()
            return
        }

        val speedTuple: SpeedResult = calculateSpeeds(speed)

        speed = when (speedType) {
            "Fly" -> speedTuple.flySpeed
            "Walk" -> speedTuple.walkSpeed
            else -> error("Unexpected speed value: ${speedType}")
        }

        if (speedType.equals("Fly", true)) targetPlayer.flySpeed = speed
        else targetPlayer.walkSpeed = speed

        val messagePath = if (isSelf) "Speed.Success" else "Speed.SuccessOther"
        command(messagePath, commandSender) {
            target(targetPlayer.name)
            postModifier { it.replace("<SPEED>", arguments[0]) }
        }.build()

        if (isSelf) return
        command("Speed.Success", targetUser) {
            target(targetPlayer.name)
            sender(commandSender.getNameSafe())
            postModifier { it.replace("<SPEED>", arguments[0]) }
        }.build()
    }

    override fun getSyntaxPath(command: Command?): String = "Speed"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Speed.Use", false)
    }

    @JvmRecord
    private data class SpeedResult(val walkSpeed: Float, val flySpeed: Float)
    companion object {
        private fun calculateSpeeds(speed: Float): SpeedResult {
            if (speed <= 0) return SpeedResult(0f, 0f)

            var flySpeed = speed / 10f
            flySpeed = Math.clamp(flySpeed, 0f, 1f)
            var walkSpeed = 0.2f + (speed - 1) * 0.0888889f
            walkSpeed = Math.clamp(walkSpeed, 0f, 1f)

            return SpeedResult(walkSpeed, flySpeed)
        }
    }
}
