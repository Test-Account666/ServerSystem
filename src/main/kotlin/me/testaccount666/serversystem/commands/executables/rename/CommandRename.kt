package me.testaccount666.serversystem.commands.executables.rename

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ComponentColor.translateToComponent
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.Command

@ServerSystemCommand("rename")
class CommandRename : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "Rename.Use"
    override fun getSyntaxPath(command: Command?) = "Rename"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return
        val player = commandSender.getPlayer()!!

        val itemInHand = player.inventory.itemInMainHand
        if (itemInHand.isAir()) {
            command("Rename.NoItemInHand", commandSender).build()
            return
        }

        val itemMeta = itemInHand.itemMeta
        var name = translateToComponent(arguments.joinToString(" ").trim())
        val nukeItalic = !name.hasDecoration(TextDecoration.ITALIC)
        if (nukeItalic) name = name.decoration(TextDecoration.ITALIC, false)

        itemMeta.itemName(name)
        itemMeta.displayName(name)
        itemInHand.setItemMeta(itemMeta)
        command("Rename.Success", commandSender).build()
    }
}
