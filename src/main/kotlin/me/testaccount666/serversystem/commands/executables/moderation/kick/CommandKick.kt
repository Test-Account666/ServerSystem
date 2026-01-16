package me.testaccount666.serversystem.commands.executables.moderation.kick

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("kick")
class CommandKick : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Moderation.Kick.Use")) return

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val targetUser = getTargetUser(commandSender, returnSender = false, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val defaultReason = command("Moderation.DefaultReason", commandSender) {
            target(targetUser.getNameOrNull())
            prefix(false)
            send(false)
            blankError(true)
        }.build()
        if (defaultReason.isEmpty()) {
            log.severe("(CommandKick) Default reason is empty! This should not happen!")
            general("ErrorOccurred", commandSender) { label(label) }.build()
            return
        }
        var reason = defaultReason

        if (arguments.size > 1) reason = arguments.drop(1).joinToString(" ").trim { it == ' ' }

        val kickMessage = command("Moderation.Kick.Kick", commandSender) {
            target(targetUser.getNameOrNull())
            prefix(false)
            postModifier { it.replace("<REASON>", reason) }
            send(false)
            blankError(true)
        }.build()

        if (kickMessage.isEmpty()) {
            log.severe("(CommandBan) Kick message is empty! This should not happen!")
            general("ErrorOccurred", commandSender).build()
            return
        }

        targetUser.getPlayer()!!.kickPlayer(kickMessage)

        command("Moderation.Kick.Success", commandSender) { target(targetUser.getNameSafe()) }.build()
    }

    override fun getSyntaxPath(command: Command?) = "Kick"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Moderation.Kick.Use", false)
    }
}

