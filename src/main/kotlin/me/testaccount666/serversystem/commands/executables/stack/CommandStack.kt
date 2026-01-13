package me.testaccount666.serversystem.commands.executables.stack

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("stack")
class CommandStack : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Stack.Use")) return
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        val itemInHand = commandSender.getPlayer()!!.inventory.itemInMainHand
        if (itemInHand.isAir()) {
            command("Stack.NoItemInHand", commandSender).build()
            return
        }

        itemInHand.amount = itemInHand.maxStackSize
        command("Stack.Success", commandSender).build()
    }

    override fun getSyntaxPath(command: Command?): String {
        throw UnsupportedOperationException("Stack command doesn't have an available syntax!")
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Stack.Use", false)
    }
}
