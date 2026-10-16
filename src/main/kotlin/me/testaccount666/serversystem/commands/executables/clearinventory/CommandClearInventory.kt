package me.testaccount666.serversystem.commands.executables.clearinventory

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

@ServerSystemCommand("clearinventory")
class CommandClearInventory : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "ClearInventory.Use"
    override fun getSyntaxPath(command: Command?) = "ClearInventory"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "ClearInventory.Other", targetPlayer.name)) return

        val inventory = targetPlayer.inventory
        inventory.clear()
        inventory.contents = arrayOf()
        inventory.armorContents = arrayOf()
        inventory.extraContents = arrayOf()
        inventory.storageContents = arrayOf()

        val messagePath = if (isSelf) "ClearInventory.Success" else "ClearInventory.SuccessOther"
        commandSender.commandMsg(messagePath) { target(targetPlayer.name) }

        if (isSelf) return

        targetUser.commandMsg("ClearInventory.Success") {
            sender(commandSender.nameSafe)
            target(targetPlayer.name)
        }
    }
}
