package me.testaccount666.serversystem.commands.executables.ping

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command

@ServerSystemCommand("ping")
class CommandPing : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "Ping.Use"
    override fun getSyntaxPath(command: Command?) = "Ping"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "Ping.Other", targetPlayer.name)) return

        val messagePath = if (isSelf) "Ping.Success" else "Ping.Other"
        command(messagePath, commandSender) {
            target(targetPlayer.name)
            postModifier { it.replace("<PING>", "${targetPlayer.ping}ms") }
        }.build()
    }
}
