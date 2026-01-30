package me.testaccount666.serversystem.commands.executables.smelt

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe

@ServerSystemCommand("smelt")
class CommandSmelt : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "Smelt.Use"
    override fun getSyntaxPath(command: Command?) = "Generic"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        val player = commandSender.getPlayer()!!
        val itemInHand = player.inventory.itemInMainHand
        if (itemInHand.isAir()) {
            command("Smelt.NoItemInHand", commandSender).build()
            return
        }

        val smeltedItem = getSmeltedItem(itemInHand) ?: run {
            command("Smelt.NoSmeltedItem", commandSender).build()
            return
        }

        smeltedItem.amount = itemInHand.amount
        player.inventory.setItemInMainHand(smeltedItem)

        command("Smelt.Success", commandSender) {
            postModifier {
                it.replace("<INPUT>", itemInHand.type.name)
                    .replace("<OUTPUT>", smeltedItem.type.name)
            }
        }.build()
    }

    private fun getSmeltedItem(itemStack: ItemStack): ItemStack? {
        return Bukkit.recipeIterator().asSequence().filterIsInstance<FurnaceRecipe>()
            .filter { it.inputChoice.test(itemStack) }.map(Recipe::getResult).firstOrNull()
    }
}
