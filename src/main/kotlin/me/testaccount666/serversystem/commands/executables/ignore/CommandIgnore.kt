package me.testaccount666.serversystem.commands.executables.ignore

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("ignore", ["unignore"])
class CommandIgnore : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.equals("ignore", true)) {
            executeIgnore(commandSender, label, *arguments)
            return
        }

        executeUnignore(commandSender, label, *arguments)
    }

    private fun executeUnignore(commandSender: User, label: String, vararg arguments: String) {
        val targetUser = validateAndGetUser(commandSender, label, "Unignore", *arguments) ?: return

        val targetPlayer = targetUser.getPlayer()

        val targetUuid = targetPlayer!!.uniqueId
        if (!commandSender.isIgnoredPlayer(targetUuid)) {
            command("Unignore.NotIgnored", commandSender) { target(targetPlayer.name) }.build()
            return
        }

        commandSender.removeIgnoredPlayer(targetUuid)
        commandSender.save()
        command("Unignore.Success", commandSender) { target(targetPlayer.name) }.build()
    }


    private fun executeIgnore(commandSender: User, label: String, vararg arguments: String) {
        val targetUser = validateAndGetUser(commandSender, label, "Ignore", *arguments) ?: return

        val targetPlayer = targetUser.getPlayer()!!

        val targetUuid = targetPlayer.uniqueId
        if (commandSender.isIgnoredPlayer(targetUuid)) {
            command("Ignore.AlreadyIgnored", commandSender) { target(targetPlayer.name) }.build()
            return
        }

        commandSender.addIgnoredPlayer(targetUuid)
        commandSender.save()
        command("Ignore.Success", commandSender) { target(targetPlayer.name) }.build()
    }

    private fun validateAndGetUser(commandSender: User, label: String, command: String?, vararg arguments: String): User? {
        if (!checkBasePermission(commandSender, "${command}.Use")) return null
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return null
        }
        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(null))
                label(label)
            }.build()
            return null
        }

        val targetUser = getTargetUser(commandSender, returnSender = false, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return null
        }

        val isSelf = targetUser === commandSender

        if (isSelf) {
            command("${command}.Self", commandSender).build()
            return null
        }

        return targetUser
    }

    override fun getSyntaxPath(command: Command?): String = "Ignore"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        val permissionPath = if (command.name.equals("ignore", true)) "Ignore.Use" else "Unignore.Use"
        return hasCommandPermission(player, permissionPath, false)
    }
}
