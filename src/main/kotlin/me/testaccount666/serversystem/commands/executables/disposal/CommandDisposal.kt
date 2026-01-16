package me.testaccount666.serversystem.commands.executables.disposal

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

@ServerSystemCommand("disposal")
class CommandDisposal : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Disposal.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkOtherPermission(commandSender, "Disposal.Other", targetPlayer.name)) return

        targetPlayer.openInventory(Bukkit.createInventory(targetPlayer, InventoryType.CHEST.defaultSize * 2, "§cTrash"))

        if (isSelf) return
        command("Disposal.Success", targetUser) { target(targetPlayer.name) }.build()
    }

    override fun getSyntaxPath(command: Command?) = "Disposal"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Disposal.Use", false)
    }
}
