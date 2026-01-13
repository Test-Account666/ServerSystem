package me.testaccount666.serversystem.commands.executables.hat

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
import org.bukkit.inventory.ItemStack

@ServerSystemCommand("hat")
class CommandHat : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Hat.Use")) return
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        val inventory = commandSender.getPlayer()!!.inventory
        val itemInHand = inventory.itemInMainHand
        val itemOnHead = inventory.helmet ?: ItemStack.empty()

        if (itemInHand.isAir() && itemOnHead.isAir()) {
            command("Hat.NoHat", commandSender).build()
            return
        }

        if (itemInHand.isAir() && !itemOnHead.isAir()) {
            inventory.helmet = ItemStack.empty()
            inventory.setItemInMainHand(itemOnHead)
            command("Hat.RemovedHat", commandSender).build()
            return
        }

        if (!itemInHand.isAir() && !itemOnHead.isAir()) {
            command("Hat.AlreadyHasHat", commandSender).build()
            return
        }

        inventory.helmet = itemInHand
        inventory.setItemInMainHand(ItemStack.empty())
        command("Hat.AppliedHat", commandSender).build()
    }

    override fun getSyntaxPath(command: Command?): String {
        throw UnsupportedOperationException("Hat command doesn't have an available syntax!")
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Hat.Use", false)
    }
}
