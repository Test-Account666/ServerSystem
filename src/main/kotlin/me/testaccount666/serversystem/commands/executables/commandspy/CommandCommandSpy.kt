package me.testaccount666.serversystem.commands.executables.commandspy

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

@ServerSystemCommand("commandspy")
class CommandCommandSpy : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "CommandSpy.Use"
    override fun getSyntaxPath(command: Command?) = "CommandSpy"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "CommandSpy.Other", targetPlayer.name)) return

        val isEnabled = !targetUser.isCommandSpyEnabled

        var messagePath = if (isSelf) "CommandSpy.Success" else "CommandSpy.SuccessOther"

        messagePath += if (isEnabled) ".Enabled" else ".Disabled"

        targetUser.isCommandSpyEnabled = isEnabled
        targetUser.save()

        commandSender.commandMsg(messagePath) { target(targetPlayer.name) }

        if (isSelf) return
        targetUser.commandMsg("CommandSpy.Success" + (if (isEnabled) "Enabled" else "Disabled"))
    }
}
