package me.testaccount666.serversystem.commands.executables.rename

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ComponentColor.Companion.translateToComponent
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("rename")
class CommandRename : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Rename.Use")) return
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }
        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }
        val player = commandSender.getPlayer()!!
        val itemInHand = player.inventory.itemInMainHand
        if (itemInHand.isAir()) {
            command("Rename.NoItemInHand", commandSender).build()
            return
        }

        val newName = arguments.joinToString(" ").trim { it <= ' ' }

        val itemMeta = itemInHand.itemMeta

        var name = translateToComponent(newName)
        val nukeItalic = !name.hasDecoration(TextDecoration.ITALIC)
        if (nukeItalic) name = name.decoration(TextDecoration.ITALIC, false)

        itemMeta.itemName(name)
        itemMeta.displayName(name)
        itemInHand.setItemMeta(itemMeta)
        command("Rename.Success", commandSender).build()
    }

    override fun getSyntaxPath(command: Command?) = "Rename"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Rename.Use", false)
    }
}
