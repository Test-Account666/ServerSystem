package me.testaccount666.serversystem.commands.executables.moderation.kick

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

@ServerSystemCommand("kick")
class CommandKick : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "Moderation.Kick.Use"
    override fun getSyntaxPath(command: Command?) = "Kick"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val targetUser = getTargetUser(commandSender, returnSender = false, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        val defaultReason = commandSender.commandMsg("Moderation.DefaultReason") {
            target(targetUser.getNameOrNull())
            prefix(false)
            send(false)
            blankError(true)
        }
        if (defaultReason.isEmpty()) {
            log.severe("(CommandKick) Default reason is empty! This should not happen!")
            commandSender.generalMsg("ErrorOccurred") { label(label) }
            return
        }
        var reason = defaultReason

        if (arguments.size > 1) reason = arguments.join(1)

        val kickMessage = commandSender.commandMsg("Moderation.Kick.Kick") {
            target(targetUser.getNameOrNull())
            prefix(false)
            postModifier { it.replace("<REASON>", reason) }
            send(false)
            blankError(true)
        }

        if (kickMessage.isEmpty()) {
            log.severe("(CommandBan) Kick message is empty! This should not happen!")
            commandSender.generalMsg("ErrorOccurred")
            return
        }

        targetUser.getPlayer()!!.kickPlayer(kickMessage)

        commandSender.commandMsg("Moderation.Kick.Success") { target(targetUser.nameSafe) }
    }
}

