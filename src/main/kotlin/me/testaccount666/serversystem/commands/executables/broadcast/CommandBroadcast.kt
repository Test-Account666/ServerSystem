package me.testaccount666.serversystem.commands.executables.broadcast

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ChatColor.Companion.translateColor
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import org.bukkit.Bukkit
import org.bukkit.command.Command

@ServerSystemCommand("broadcast")
class CommandBroadcast : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "Broadcast.Use"
    override fun getSyntaxPath(command: Command?) = "Broadcast"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val joined = arguments.joinToString(" ").trim()
        val broadcast = translateColor(joined)

        command("Broadcast.Format", commandSender) {
            target("*")
            prefix(false)
            send(false)
            postModifier { it.replace("<BROADCAST>", broadcast) }
            blankError(true)
        }.build().takeIf { !it.isEmpty() }?.also(Bukkit::broadcastMessage)
    }
}
