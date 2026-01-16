package me.testaccount666.serversystem.commands.executables.fly

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player

/**
 * Command executor for the fly command.
 * This command allows players to toggle flight mode for themselves or other players.
 */
@ServerSystemCommand("fly")
class CommandFly : AbstractServerSystemCommand() {
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
        if (!checkBasePermission(commandSender, "Fly.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments)

        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkOtherPermission(commandSender, "Fly.Other", targetPlayer.name)) return

        val isFlying = !targetPlayer.allowFlight
        var messagePath = if (isSelf) "Fly.Success" else "Fly.SuccessOther"
        messagePath = if (isFlying) "${messagePath}.Enabled" else "${messagePath}.Disabled"

        val isOnGround = targetPlayer.location.add(0.0, -.3, 0.0).block.type.isSolid

        targetPlayer.allowFlight = isFlying
        if (!isOnGround) targetPlayer.isFlying = isFlying

        command(messagePath, commandSender) { target(targetPlayer.name) }.build()

        if (isSelf) return
        command("Fly.Success." + (if (isFlying) "Enabled" else "Disabled"), targetUser) {
            sender(commandSender.getNameSafe())
        }.build()
    }

    override fun getSyntaxPath(command: Command?) = "Fly"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Fly.Use", false)
    }
}
