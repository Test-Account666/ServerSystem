package me.testaccount666.serversystem.commands.executables.clearinventory

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("clearinventory")
class CommandClearInventory : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "ClearInventory.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkOtherPermission(commandSender, "ClearInventory.Other", targetPlayer.name)) return

        val inventory = targetPlayer.inventory
        inventory.clear()
        inventory.contents = arrayOfNulls(0)
        inventory.armorContents = arrayOfNulls(0)
        inventory.extraContents = arrayOfNulls(0)
        inventory.storageContents = arrayOfNulls(0)

        val messagePath = if (isSelf) "ClearInventory.Success" else "ClearInventory.SuccessOther"
        command(messagePath, commandSender) { target(targetPlayer.name) }.build()


        if (isSelf) return

        command("ClearInventory.Success", targetUser) {
            sender(commandSender.getNameSafe())
            target(targetPlayer.name)
        }.build()
    }

    override fun getSyntaxPath(command: Command?) = "ClearInventory"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "ClearInventory.Use", false)
    }
}
