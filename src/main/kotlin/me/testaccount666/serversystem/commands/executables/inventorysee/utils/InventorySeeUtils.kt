package me.testaccount666.serversystem.commands.executables.inventorysee.utils

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.function.BiConsumer

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
        val markerItem = ItemStack(material)
        val itemMeta = markerItem.itemMeta
        itemMeta.displayName(
            Component.text(displayName)
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .color(TextColor.color(255, 0, 0))
                .asComponent()
        )
        markerItem.setItemMeta(itemMeta)

        for (slot in startSlot..<endSlot) inventory.setItem(slot, markerItem)
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
        inventoryAction: BiConsumer<User, String>
    ) {
        val viewers = ArrayList(inventory.viewers)
        inventory.close()

        Bukkit.getScheduler().runTaskLater(instance, Runnable {
            viewers.forEach { viewer ->
                if (viewer !is Player) return@forEach
                val registry = instance.registry
                val userManager = registry.getService<UserManager>()
                val cachedUser = userManager.getUserOrNull(viewer) ?: return@forEach

                if (cachedUser.isOfflineUser) return@forEach
                val user = cachedUser.offlineUser as User
                inventoryAction.accept(user, player.name)
            }
        }, delayTicks)
    }
}