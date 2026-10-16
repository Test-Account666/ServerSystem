package me.testaccount666.serversystem.commands.executables.god

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command
import org.bukkit.entity.Mob

/**
 * Command executor for the god command.
 * This command allows players to toggle god mode (invulnerability) for themselves or other players.
 */
@ServerSystemCommand("god")
class CommandGod : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "God.Use"
    override fun getSyntaxPath(command: Command?) = "God"

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
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "God.Other", targetPlayer.name)) return

        val isGod = !targetUser.isGodMode
        var messagePath = if (isSelf) "God.Success" else "God.SuccessOther"
        messagePath = if (isGod) "${messagePath}.Enabled" else "${messagePath}.Disabled"

        targetUser.isGodMode = isGod
        targetUser.save()

        targetPlayer.location.getNearbyEntitiesByType(Mob::class.java, 16.0) {
            it.target?.uniqueId == targetPlayer.uniqueId
        }.forEach { it.target = null }

        commandSender.commandMsg(messagePath) { target(targetPlayer.name) }

        if (isSelf) return
        targetUser.commandMsg("God.Success." + (if (isGod) "Enabled" else "Disabled")) {
            sender(commandSender.nameSafe)
            target(targetPlayer.name)
        }
    }
}
