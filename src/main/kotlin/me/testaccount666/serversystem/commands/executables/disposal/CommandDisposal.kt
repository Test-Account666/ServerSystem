package me.testaccount666.serversystem.commands.executables.disposal

import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.event.inventory.InventoryType

@ServerSystemCommand("disposal")
class CommandDisposal : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "Disposal.Use"
    override fun getSyntaxPath(command: Command?) = "Disposal"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "Disposal.Other", targetPlayer.name)) return

        val inventory = Bukkit.createInventory(targetPlayer, InventoryType.CHEST.defaultSize * 2, "&cTrash".asComponent())

        targetPlayer.openInventory(inventory)

        if (isSelf) return
        targetUser.commandMsg("Disposal.Success") { target(targetPlayer.name) }
    }
}
