package me.testaccount666.serversystem.commands.executables.clearchat

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("clearchat")
class CommandClearChat : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "ClearChat.Use")) return

        Bukkit.getOnlinePlayers().forEach { everyone ->
            if (!hasCommandPermission(everyone, "ClearChat.Bypass", false)) {
                for (index in 0..<_CLEAR_LINES) {
                    val randomChar = (33 + (Math.random() * (126 - 33))).toInt().toChar() // Safe ASCII range
                    everyone.sendMessage(randomChar.toString())
                }
                for (index in 0..<_CLEAR_LINES) everyone.sendMessage(_EMPTY_LINE)
            }
        }

        command("ClearChat.Success", commandSender) {
            send(false)
            blankError(true)
        }.build().takeIf { it.isNotEmpty() }?.also { Bukkit.broadcastMessage(it) }
    }

    override fun getSyntaxPath(command: Command?): String {
        throw UnsupportedOperationException("ClearChat command doesn't have an available syntax!")
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "ClearChat.Use", false)
    }

    companion object {
        private const val _CLEAR_LINES = 300
        private const val _EMPTY_LINE = "§r "
    }
}
