package me.testaccount666.serversystem.commands.executables.clearchat

import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command

@ServerSystemCommand("clearchat")
class CommandClearChat : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "ClearChat.Use"
    override fun getSyntaxPath(command: Command?) = "Generic"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        Bukkit.getOnlinePlayers().forEach { everyone ->
            if (hasCommandPermission(everyone, "ClearChat.Bypass", false)) return@forEach

            repeat(_CLEAR_LINES) {
                val randomChar = (33 + (Math.random() * (126 - 33))).toInt().toChar() // Safe ASCII range
                everyone.sendMessage(randomChar.toString())
            }
            repeat(_CLEAR_LINES) { everyone.sendMessage(_EMPTY_LINE) }
        }

        commandSender.commandMsg("ClearChat.Success") {
            send(false)
            blankError(true)
        }.takeIf { it.isNotEmpty() }?.asComponent()?.also(Bukkit::broadcast)
    }

    companion object {
        private const val _CLEAR_LINES = 300
        private const val _EMPTY_LINE = "§r "
    }
}
