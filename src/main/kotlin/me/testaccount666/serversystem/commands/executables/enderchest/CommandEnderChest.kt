package me.testaccount666.serversystem.commands.executables.enderchest

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.executables.enderchest.offline.CommandOfflineEnderChest
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("enderchest", ["offlineenderchest"])
class CommandEnderChest : AbstractServerSystemCommand() {
    val offlineEnderChest: CommandOfflineEnderChest = CommandOfflineEnderChest()

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.startsWith("offline", true)) {
            offlineEnderChest.execute(commandSender, command, label, *arguments)
            return
        }

        executeEnderChestCommand(commandSender, *arguments)
    }

    fun executeEnderChestCommand(commandSender: User, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "EnderChest.Use")) return

        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        val targetUser = getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkOtherPermission(commandSender, "EnderChest.Other", targetPlayer.name)) return

        commandSender.getPlayer()!!.openInventory(targetPlayer.enderChest)
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "EnderChest"
        if (command.name.startsWith("offline", true)) return offlineEnderChest.getSyntaxPath(command)
        return "EnderChest"
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "EnderChest.Use", false)
    }
}
