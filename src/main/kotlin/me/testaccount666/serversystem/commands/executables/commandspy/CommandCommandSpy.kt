package me.testaccount666.serversystem.commands.executables.commandspy

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("commandspy")
class CommandCommandSpy : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "CommandSpy.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkOtherPermission(commandSender, "CommandSpy.Other", targetPlayer.name)) return

        val isEnabled = !targetUser.isCommandSpyEnabled

        var messagePath = if (isSelf) "CommandSpy.Success" else "CommandSpy.SuccessOther"

        messagePath += if (isEnabled) ".Enabled" else ".Disabled"

        targetUser.isCommandSpyEnabled = isEnabled
        targetUser.save()

        command(messagePath, commandSender) { target(targetPlayer.name) }.build()

        if (isSelf) return
        command("CommandSpy.Success" + (if (isEnabled) "Enabled" else "Disabled"), targetUser).build()
    }

    override fun getSyntaxPath(command: Command?): String = "CommandSpy"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "CommandSpy.Use", false)
    }
}
