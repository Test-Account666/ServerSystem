package me.testaccount666.serversystem.commands.executables.hat

import me.testaccount666.paperktx.extensions.*
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command
import org.bukkit.inventory.EquipmentSlot

@ServerSystemCommand("hat")
class CommandHat : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "Hat.Use"
    override fun getSyntaxPath(command: Command?) = "Generic"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        val inventory = commandSender.getPlayer()!!.inventory
        val itemInHand = inventory[EquipmentSlot.HAND]
        val itemOnHead = inventory[EquipmentSlot.HEAD]

        if (itemInHand.isAir() && itemOnHead.isAir()) {
            commandSender.commandMsg("Hat.NoHat")
            return
        }

        if (itemInHand.isAir() && !itemOnHead.isAir()) {
            inventory[EquipmentSlot.HEAD] = null
            inventory[EquipmentSlot.HAND] = itemOnHead
            commandSender.commandMsg("Hat.RemovedHat")
            return
        }

        if (!itemInHand.isAir() && !itemOnHead.isAir()) {
            commandSender.commandMsg("Hat.AlreadyHasHat")
            return
        }

        inventory[EquipmentSlot.HEAD] = itemInHand
        inventory[EquipmentSlot.HAND] = null
        commandSender.commandMsg("Hat.AppliedHat")
    }
}
