package me.testaccount666.serversystem.commands.executables.fly

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

/**
 * Command executor for the fly command.
 * This command allows players to toggle flight mode for themselves or other players.
 */
@ServerSystemCommand("fly")
class CommandFly : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "Fly.Use"
    override fun getSyntaxPath(command: Command?) = "Fly"

    /**
     * Executes the fly command.
     * This method toggles flight mode for the target player if the sender has the required permissions.
     * If no target is specified, the sender is used as the target.
     *
     * @param commandSender The user who executed the command
     * @param command       The command that was executed
     * @param label         The alias of the command that was used
     * @param arguments     The arguments passed to the command, where the first argument can be a target player name
     */
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "Fly.Other", targetPlayer.name)) return

        val isFlying = !targetPlayer.allowFlight
        var messagePath = if (isSelf) "Fly.Success" else "Fly.SuccessOther"
        messagePath = if (isFlying) "${messagePath}.Enabled" else "${messagePath}.Disabled"

        val isOnGround = targetPlayer.location.add(0.0, -.3, 0.0).block.type.isSolid

        targetPlayer.allowFlight = isFlying
        if (!isOnGround) targetPlayer.isFlying = isFlying

        commandSender.commandMsg(messagePath) { target(targetPlayer.name) }

        if (isSelf) return
        targetUser.commandMsg("Fly.Success." + (if (isFlying) "Enabled" else "Disabled")) {
            sender(commandSender.nameSafe)
        }
    }
}
