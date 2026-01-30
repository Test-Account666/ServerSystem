package me.testaccount666.serversystem.commands.executables.kit.manager

import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class Kit(val name: String, val coolDown: Long, val inventoryContents: Array<ItemStack?>) {
    val displayName by lazy { name.lowercase().replaceFirstChar { it.uppercase() } }

    fun giveKit(player: Player) {
        val inventory = player.inventory
        val overflowItems = mutableListOf<ItemStack>()

        handleInventoryItems(inventory, overflowItems)
        dropOverflowItems(player, overflowItems)
    }

    private fun handleInventoryItems(inventory: Inventory, overflowItems: MutableList<ItemStack>) {
        inventoryContents.forEachIndexed { index, newInventoryItem ->
            newInventoryItem ?: return@forEachIndexed

            if (!inventory.getItem(index).isAir()) {
                overflowItems.add(newInventoryItem)
                return@forEachIndexed
            }

            inventory.setItem(index, newInventoryItem)
        }
    }

    private fun dropOverflowItems(player: Entity, overflowItems: List<ItemStack>) {
        if (overflowItems.isEmpty()) return

        for (item in overflowItems) player.world.dropItem(player.location, item) { it.owner = player.uniqueId }
    }
}
