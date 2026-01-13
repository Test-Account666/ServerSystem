package me.testaccount666.serversystem.commands.executables.smelt

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack

@ServerSystemCommand("smelt")
class CommandSmelt : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Smelt.Use")) return
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        val player = commandSender.getPlayer()!!
        val itemInHand = player.inventory.itemInMainHand
        if (itemInHand.isAir()) {
            command("Smelt.NoItemInHand", commandSender).build()
            return
        }

        val smeltedItem = getSmeltedItem(itemInHand)
        if (smeltedItem == null) {
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
        val recipeIterator = Bukkit.recipeIterator()

        while (recipeIterator.hasNext()) {
            val recipe = recipeIterator.next()
            if (recipe !is FurnaceRecipe) continue
            if (!recipe.inputChoice.test(itemStack)) continue

            return recipe.result
        }

        return null
    }

    override fun getSyntaxPath(command: Command?): String {
        throw UnsupportedOperationException("Smelt command doesn't have an available syntax!")
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Smelt.Use", false)
    }
}
