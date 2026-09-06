package me.testaccount666.serversystem.commands.executables.stack

import me.testaccount666.paperktx.extensions.get
import me.testaccount666.paperktx.extensions.isAir
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command
import org.bukkit.inventory.EquipmentSlot

@ServerSystemCommand("stack")
class CommandStack : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "Stack.Use"
    override fun getSyntaxPath(command: Command?) = "Generic"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        val itemInHand = commandSender.getPlayer()?.inventory[EquipmentSlot.HAND].takeUnless { it.isAir() } ?: run {
            commandSender.commandMsg("Stack.NoItemInHand")
            return
        }

        itemInHand.amount = itemInHand.maxStackSize
        commandSender.commandMsg("Stack.Success")
    }
}
