package me.testaccount666.serversystem.commands.executables.ignore

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

@ServerSystemCommand("ignore", ["unignore"])
class CommandIgnore : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "${command.name.lowercase().replaceFirstChar { it.uppercase() }}.Use"
    override fun getSyntaxPath(command: Command?) = "Ignore"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.equals("ignore", true)) {
            executeIgnore(commandSender, label, *arguments)
            return
        }

        executeUnignore(commandSender, label, *arguments)
    }


    private fun executeUnignore(commandSender: User, label: String, vararg arguments: String) {
        val targetUser = validateAndGetUser(commandSender, label, "Unignore", *arguments) ?: return
        val targetPlayer = targetUser.getPlayer()!!

        val targetUuid = targetPlayer.uniqueId
        if (!commandSender.isIgnoredPlayer(targetUuid)) {
            commandSender.commandMsg("Unignore.NotIgnored") { target(targetPlayer.name) }
            return
        }

        commandSender.removeIgnoredPlayer(targetUuid)
        commandSender.save()
        commandSender.commandMsg("Unignore.Success") { target(targetPlayer.name) }
    }

    private fun executeIgnore(commandSender: User, label: String, vararg arguments: String) {
        val targetUser = validateAndGetUser(commandSender, label, "Ignore", *arguments) ?: return
        val targetPlayer = targetUser.getPlayer()!!

        val targetUuid = targetPlayer.uniqueId
        if (commandSender.isIgnoredPlayer(targetUuid)) {
            commandSender.commandMsg("Ignore.AlreadyIgnored") { target(targetPlayer.name) }
            return
        }

        commandSender.addIgnoredPlayer(targetUuid)
        commandSender.save()
        commandSender.commandMsg("Ignore.Success") { target(targetPlayer.name) }
    }

    private fun validateAndGetUser(commandSender: User, label: String, command: String?, vararg arguments: String): User? {
        if (!isPlayer(commandSender)) return null

        val targetUser = getTargetUser(commandSender, returnSender = false, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return null
        }

        val isSelf = targetUser === commandSender

        if (isSelf) {
            commandSender.commandMsg("${command}.Self")
            return null
        }

        return targetUser
    }
}
