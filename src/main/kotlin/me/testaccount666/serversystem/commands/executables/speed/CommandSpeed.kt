package me.testaccount666.serversystem.commands.executables.speed

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

@ServerSystemCommand("speed", ["flyspeed", "walkspeed"], TabCompleterSpeed::class)
class CommandSpeed : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "Speed.Use"
    override fun getSyntaxPath(command: Command?) = "Speed"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, 1, *arguments)) return

        val targetUser = getTargetUser(commandSender, 1, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[1]) }
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

        val speed = (arguments[0].toFloatOrNull() ?: run {
            commandSender.generalMsg("InvalidArguments") {
                label(label)
                target(targetPlayer.name)
                syntax(getSyntaxPath(command))
            }
            return
        }).coerceIn(0f, 10f)

        val speedTuple = calculateSpeeds(speed)

        val actualSpeed = when (speedType) {
            "Fly" -> speedTuple.flySpeed
            "Walk" -> speedTuple.walkSpeed
            else -> error("Unexpected speed value: ${speedType}")
        }.coerceIn(0f, 1f)

        if (speedType.equals("Fly", true)) targetPlayer.flySpeed = actualSpeed
        else targetPlayer.walkSpeed = actualSpeed

        val speedString = String.format("%.2f", speed)
        val messagePath = if (isSelf) "Speed.Success" else "Speed.SuccessOther"
        commandSender.commandMsg(messagePath) {
            target(targetPlayer.name)
            postModifier { it.replace("<SPEED>", speedString) }
        }

        if (isSelf) return
        targetUser.commandMsg("Speed.Success") {
            target(targetPlayer.name)
            sender(commandSender.nameSafe)
            postModifier { it.replace("<SPEED>", speedString) }
        }
    }

    private data class SpeedResult(val walkSpeed: Float, val flySpeed: Float)
    companion object {
        private fun calculateSpeeds(speed: Float): SpeedResult {
            if (speed <= 0) return SpeedResult(0f, 0f)

            val flySpeed = (speed / 10f).coerceIn(0f, 1f)
            val walkSpeed = (0.2f + (speed - 1) * 0.0888889f).coerceIn(0f, 1f)
            return SpeedResult(walkSpeed, flySpeed)
        }
    }
}
