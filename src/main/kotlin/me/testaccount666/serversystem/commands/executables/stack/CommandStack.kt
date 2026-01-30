package me.testaccount666.serversystem.commands.executables.stack

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import org.bukkit.command.Command

@ServerSystemCommand("stack")
class CommandStack : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "Stack.Use"
    override fun getSyntaxPath(command: Command?) = "Generic"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        val itemInHand = commandSender.getPlayer()!!.inventory.itemInMainHand
        if (itemInHand.isAir()) {
            command("Stack.NoItemInHand", commandSender).build()
            return
        }

        itemInHand.amount = itemInHand.maxStackSize
        command("Stack.Success", commandSender).build()
    }
}
