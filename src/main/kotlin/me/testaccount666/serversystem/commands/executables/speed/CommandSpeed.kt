package me.testaccount666.serversystem.commands.executables.speed

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command

@ServerSystemCommand("speed", ["flyspeed", "walkspeed"], TabCompleterSpeed::class)
class CommandSpeed : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "Speed.Use"
    override fun getSyntaxPath(command: Command?) = "Speed"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, 1, *arguments)) return

        val targetUser = getTargetUser(commandSender, 1, arguments = arguments) ?: run {
            general("PlayerNotFound", commandSender) { target(arguments[1]) }.build()
            return
        }
        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "Speed.Other", targetPlayer.name)) return

        val speedType = when (command.name.lowercase()) {
            "flyspeed" -> "Fly"
            "walkspeed" -> "Walk"
            else -> if (targetPlayer.isFlying) "Fly" else "Walk"
        }

        var speed = (arguments[0].toFloatOrNull() ?: run {
            command("Speed.InvalidSpeed", commandSender) { target(targetPlayer.name) }.build()
            return
        }).coerceIn(0f, 10f)

        val speedTuple = calculateSpeeds(speed)

        speed = when (speedType) {
            "Fly" -> speedTuple.flySpeed
            "Walk" -> speedTuple.walkSpeed
            else -> error("Unexpected speed value: ${speedType}")
        }.coerceIn(0f, 1f)

        if (speedType.equals("Fly", true)) targetPlayer.flySpeed = speed
        else targetPlayer.walkSpeed = speed

        val speedString = String.format("%.2f", speed * 10F)
        val messagePath = if (isSelf) "Speed.Success" else "Speed.SuccessOther"
        command(messagePath, commandSender) {
            target(targetPlayer.name)
            postModifier { it.replace("<SPEED>", speedString) }
        }.build()

        if (isSelf) return
        command("Speed.Success", targetUser) {
            target(targetPlayer.name)
            sender(commandSender.getNameSafe())
            postModifier { it.replace("<SPEED>", speedString) }
        }.build()
    }

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
