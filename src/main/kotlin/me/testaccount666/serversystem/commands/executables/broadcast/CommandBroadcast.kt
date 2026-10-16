package me.testaccount666.serversystem.commands.executables.broadcast

import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.join
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command

@ServerSystemCommand("broadcast")
class CommandBroadcast : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "Broadcast.Use"
    override fun getSyntaxPath(command: Command?) = "Broadcast"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        commandSender.commandMsg("Broadcast.Format") {
            target("*")
            prefix(false)
            send(false)
            postModifier { it.replace("<BROADCAST>", arguments.join()) }
            blankError(true)
        }.takeIf { it.isNotEmpty() }?.asComponent()?.also(Bukkit::broadcast)
    }
}
