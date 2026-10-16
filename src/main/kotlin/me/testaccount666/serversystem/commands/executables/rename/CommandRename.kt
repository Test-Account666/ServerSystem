package me.testaccount666.serversystem.commands.executables.rename

import me.testaccount666.paperktx.extensions.*
import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.join
import me.testaccount666.serversystem.userdata.User
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.Command
import org.bukkit.inventory.EquipmentSlot

@ServerSystemCommand("rename")
class CommandRename : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "Rename.Use"
    override fun getSyntaxPath(command: Command?) = "Rename"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return
        val player = commandSender.getPlayer()!!

        val itemInHand = player.inventory[EquipmentSlot.HAND]
        if (itemInHand.isAir()) {
            commandSender.commandMsg("Rename.NoItemInHand")
            return
        }

        itemInHand.edit {
            var name = arguments.join().asComponent()
            val nukeItalic = !name.hasDecoration(TextDecoration.ITALIC)
            if (nukeItalic) name = name.decoration(TextDecoration.ITALIC, false)

            itemName(name)
            displayName(name)
        }

        commandSender.commandMsg("Rename.Success")
    }
}
