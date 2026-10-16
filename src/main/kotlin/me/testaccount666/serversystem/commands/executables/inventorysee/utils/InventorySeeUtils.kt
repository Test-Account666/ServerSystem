package me.testaccount666.serversystem.commands.executables.inventorysee.utils

import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.paperktx.extensions.itemStack
import me.testaccount666.paperktx.extensions.set
import me.testaccount666.paperktx.scheduler.skedule.okkero.schedule
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

/**
 * Utility class for common inventory see operations.
 * Extracts duplicate code from inventory see related classes.
 */
object InventorySeeUtils {
    /**
     * Places filled markers in the inventory.
     *
     * @param inventory   The inventory to place markers in
     * @param material    The material to use for the markers
     * @param displayName The display name for the markers
     * @param startSlot   The starting slot
     * @param endSlot     The ending slot (exclusive)
     */
    fun placeFilledMarkers(inventory: Inventory, material: Material, displayName: String, startSlot: Int, endSlot: Int) {
        inventory[startSlot..<endSlot] = itemStack(material) { itemName("&#FF0000${displayName}".asComponent()) }
    }

    /**
     * Adds section decorators to the inventory.
     *
     * @param displayInventory The inventory to add decorators to
     * @param isOffline        Whether this is for an offline inventory
     */
    fun addSectionDecorators(displayInventory: Inventory, isOffline: Boolean) {
        placeFilledMarkers(displayInventory, Material.ARMOR_STAND, "Armor", 45, 49)
        placeFilledMarkers(displayInventory, Material.APPLE, "Off-Hand", 49, 50)

        if (isOffline) {
            placeFilledMarkers(displayInventory, Material.BARRIER, "Unused", 50, 54)
            placeFilledMarkers(displayInventory, Material.BARRIER, "Unused", 41, 45)
        } else {
            placeFilledMarkers(displayInventory, Material.WHITE_WOOL, "Cursor", 50, 51)
            placeFilledMarkers(displayInventory, Material.DROPPER, "Drop item", 51, 54)
        }
    }

    /**
     * Handles inventory viewers when a player quits or joins.
     *
     * @param inventory       The inventory being viewed
     * @param player          The player who quit or joined
     * @param delayTicks      The delay in ticks before processing
     * @param inventoryAction The action to perform for each viewer
     */
    fun handleInventoryViewers(
        inventory: Inventory, player: Player, delayTicks: Long,
        inventoryAction: (User, String) -> Unit,
    ) {
        val viewers = ArrayList(inventory.viewers)
        inventory.close()

        instance.schedule {
            waitFor(delayTicks)
            viewers.asSequence().mapNotNull { it as? Player }.forEach {
                val cachedUser = getService<UserManager>().getUserOrNull(it) ?: return@forEach

                if (cachedUser.isOfflineUser) return@forEach
                inventoryAction(cachedUser.onlineUser, player.name)
            }
        }
    }
}