package me.testaccount666.serversystem.commands.executables.god

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player

/**
 * Command executor for the god command.
 * This command allows players to toggle god mode (invulnerability) for themselves or other players.
 */
@ServerSystemCommand("god")
class CommandGod : AbstractServerSystemCommand() {
    /**
     * Executes the god command.
     * This method toggles god mode (invulnerability) for the target player if the sender has the required permissions.
     * If no target is specified, the sender is used as the target.
     * 
     * @param commandSender The user who executed the command
     * @param command       The command that was executed
     * @param label         The alias of the command that was used
     * @param arguments     The arguments passed to the command, where the first argument can be a target player name
     */
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "God.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkOtherPermission(commandSender, "God.Other", targetPlayer.name)) return

        val isGod = !targetUser.isGodMode
        var messagePath = if (isSelf) "God.Success" else "God.SuccessOther"
        messagePath = if (isGod) "${messagePath}.Enabled" else "${messagePath}.Disabled"

        targetUser.isGodMode = isGod
        targetUser.save()

        command(messagePath, commandSender) { target(targetPlayer.name) }.build()

        if (isSelf) return
        command("God.Success." + (if (isGod) "Enabled" else "Disabled"), targetUser) {
            sender(commandSender.getNameSafe())
            target(targetPlayer.name)
        }.build()
    }

    override fun getSyntaxPath(command: Command?): String = "God"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "God.Use", false)
    }
}
