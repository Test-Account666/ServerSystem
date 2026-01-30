package me.testaccount666.serversystem.commands.executables.enderchest.online

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.executables.enderchest.offline.CommandOfflineEnderChest
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command

@ServerSystemCommand("enderchest", ["offlineenderchest"], TabCompleterEnderChest::class)
class CommandEnderChest : AbstractServerSystemCommand() {
    val offlineEnderChest: CommandOfflineEnderChest = CommandOfflineEnderChest()
    override fun minRequiredArguments(command: Command): Int {
        if (isOffline(command)) return offlineEnderChest.minRequiredArguments(command)
        return 0
    }

    override fun getUsagePermission(command: Command): String {
        if (isOffline(command)) return offlineEnderChest.getUsagePermission(command)
        return "EnderChest.Use"
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "EnderChest"
        if (isOffline(command)) return offlineEnderChest.getSyntaxPath(command)
        return "EnderChest"
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isOffline(command)) {
            offlineEnderChest.execute(commandSender, command, label, *arguments)
            return
        }

        executeEnderChestCommand(commandSender, *arguments)
    }

    fun executeEnderChestCommand(commandSender: User, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "EnderChest.Other", targetPlayer.name)) return

        commandSender.getPlayer()!!.openInventory(targetPlayer.enderChest)
    }

    private fun isOffline(command: Command) = command.name.startsWith("offline", true)
}
