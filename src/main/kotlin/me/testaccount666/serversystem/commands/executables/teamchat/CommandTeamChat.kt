package me.testaccount666.serversystem.commands.executables.teamchat

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("teamchat")
class CommandTeamChat : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "TeamChat.Use")) return
        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val message = arguments.joinToString { " " }.trim { it <= ' ' }
        val format = command("TeamChat.Format", commandSender) {
            send(false)
            prefix(false)
            blankError(true)
            postModifier { format -> format.replace("<MESSAGE>", message) }
        }.build()
        if (format.isEmpty()) return

        Bukkit.getOnlinePlayers().forEach { everyone ->
            if (!hasCommandPermission(everyone, "TeamChat.Use", false)) return@forEach
            everyone.sendMessage(format)
        }
    }

    override fun getSyntaxPath(command: Command?): String = "TeamChat"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "TeamChat.Use", false)
    }
}
