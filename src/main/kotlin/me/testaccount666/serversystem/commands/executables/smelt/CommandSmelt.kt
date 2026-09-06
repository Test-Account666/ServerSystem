package me.testaccount666.serversystem.commands.executables.smelt

import me.testaccount666.paperktx.extensions.*
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.inventory.*

@ServerSystemCommand("smelt")
class CommandSmelt : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "Smelt.Use"
    override fun getSyntaxPath(command: Command?) = "Generic"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        val player = commandSender.getPlayer()!!
        val itemInHand = player.inventory[EquipmentSlot.HAND]
        if (itemInHand.isAir()) {
            commandSender.commandMsg("Smelt.NoItemInHand")
            return
        }

        val smeltedItem = getSmeltedItem(itemInHand) ?: run {
            commandSender.commandMsg("Smelt.NoSmeltedItem")
            return
        }

        smeltedItem.amount = itemInHand.amount
        player.inventory[EquipmentSlot.HAND] = (smeltedItem)

        commandSender.commandMsg("Smelt.Success") {
            postModifier {
                it.replace("<INPUT>", itemInHand.type.name)
                    .replace("<OUTPUT>", smeltedItem.type.name)
            }
        }
    }

    private fun getSmeltedItem(itemStack: ItemStack): ItemStack? {
        return Bukkit.recipeIterator().asSequence().filterIsInstance<FurnaceRecipe>()
            .filter { it.inputChoice.test(itemStack) }.map(Recipe::getResult).firstOrNull()
    }
}
