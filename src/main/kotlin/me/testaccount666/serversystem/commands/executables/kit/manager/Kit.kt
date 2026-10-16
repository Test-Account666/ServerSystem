package me.testaccount666.serversystem.commands.executables.kit.manager

import me.testaccount666.paperktx.extensions.*
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class Kit(name: String, val coolDown: Long, val inventoryContents: Array<ItemStack?>) {
    val name = name.lowercase()
    val displayName by lazy { this.name.replaceFirstChar { it.uppercase() } }

    fun giveKit(player: Player) {
        val inventory = player.inventory
        val overflowItems = mutableListOf<ItemStack>()

        handleInventoryItems(inventory, overflowItems)
        dropOverflowItems(player, overflowItems)
    }

    private fun handleInventoryItems(inventory: Inventory, overflowItems: MutableList<ItemStack>) {
        inventoryContents.forEachIndexed { index, newInventoryItem ->
            newInventoryItem ?: return@forEachIndexed

            if (!inventory[index].isAir()) {
                overflowItems.add(newInventoryItem)
                return@forEachIndexed
            }

            inventory[index] = newInventoryItem
        }
    }

    private fun dropOverflowItems(player: Entity, overflowItems: List<ItemStack>) {
        if (overflowItems.isEmpty()) return

        for (item in overflowItems) player.world.dropItem(player.location, item) { it.owner = player.uniqueId }
    }
}
