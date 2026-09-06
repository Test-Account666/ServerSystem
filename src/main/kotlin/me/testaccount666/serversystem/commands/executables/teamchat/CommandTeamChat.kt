package me.testaccount666.serversystem.commands.executables.teamchat

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.join
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command

@ServerSystemCommand("teamchat")
class CommandTeamChat : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "TeamChat.Use"
    override fun getSyntaxPath(command: Command?) = "TeamChat"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val message = arguments.join()
        val format = commandSender.commandMsg("TeamChat.Format") {
            send(false)
            prefix(false)
            blankError(true)
            postModifier { it.replace("<MESSAGE>", message) }
        }.takeIf(String::isNotEmpty) ?: return

        Bukkit.getOnlinePlayers().forEach { everyone ->
            if (!hasCommandAccess(everyone, command)) return@forEach
            everyone.sendMessage(format)
        }
    }
}
