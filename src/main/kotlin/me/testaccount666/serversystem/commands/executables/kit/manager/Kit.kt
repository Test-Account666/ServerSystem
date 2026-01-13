package me.testaccount666.serversystem.commands.executables.kit.manager

import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import java.util.Locale.getDefault

class Kit(
    val name: String,
    val coolDown: Long,
    val offHandItem: ItemStack?,
    val armorContents: Array<ItemStack?>,
    val inventoryContents: Array<ItemStack?>
) {
    private var _displayName: String? = null

    val displayName: String
        get() {
            if (_displayName != null) return _displayName!!
            _displayName = name.lowercase(getDefault()).replaceFirstChar { it.uppercase() }

            return _displayName!!
        }

    fun giveKit(player: Player) {
        val inventory = player.inventory
        val overflowItems = ArrayList<ItemStack>()

        handleOffHandItem(inventory, overflowItems)
        handleArmorItems(inventory, overflowItems)
        handleInventoryItems(inventory, overflowItems)
        dropOverflowItems(player, overflowItems)
    }

    private fun handleOffHandItem(inventory: PlayerInventory, overflowItems: MutableList<ItemStack>) {
        if (offHandItem == null) return
        val currentOffHandItem = inventory.itemInOffHand

        if (!currentOffHandItem.isAir()) {
            overflowItems.add(offHandItem)
            return
        }

        inventory.setItemInOffHand(offHandItem)
    }

    private fun handleArmorItems(inventory: PlayerInventory, overflowItems: MutableList<ItemStack>) {
        val slots = arrayOf(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)
        for (index in armorContents.indices) {
            val currentItem = inventory.getItem(slots[index])
            val newArmorItem = armorContents[index] ?: continue

            if (!currentItem.isAir()) {
                overflowItems.add(newArmorItem)
                continue
            }

            inventory.setItem(slots[index], newArmorItem)
        }
    }

    private fun handleInventoryItems(inventory: Inventory, overflowItems: MutableList<ItemStack>) {
        for (index in inventoryContents.indices) {
            val currentItem = inventory.getItem(index)
            val newInventoryItem = inventoryContents[index] ?: continue

            if (!currentItem.isAir()) {
                overflowItems.add(newInventoryItem)
                continue
            }

            inventory.setItem(index, newInventoryItem)
        }
    }

    private fun dropOverflowItems(player: Entity, overflowItems: MutableList<ItemStack>) {
        if (overflowItems.isEmpty()) return

        for (item in overflowItems) player.world.dropItem(player.location, item) { it.owner = player.uniqueId }
    }
}
