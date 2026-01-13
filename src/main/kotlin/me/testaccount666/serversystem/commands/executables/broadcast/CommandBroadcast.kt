package me.testaccount666.serversystem.commands.executables.broadcast

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ChatColor.Companion.translateColor
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit.broadcastMessage
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("broadcast")
class CommandBroadcast : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Broadcast.Use")) return

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val joined = arguments.joinToString(" ").trim { it <= ' ' }
        val broadcast = translateColor(joined)

        command("Broadcast.Format", commandSender) {
            target("*")
            prefix(false)
            send(false)
            postModifier { it.replace("<BROADCAST>", broadcast) }
            blankError(true)
        }.build().takeIf { it.isEmpty() }?.also { broadcastMessage(it) }
    }

    override fun getSyntaxPath(command: Command?): String = "Broadcast"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Broadcast.Use", false)
    }
}
