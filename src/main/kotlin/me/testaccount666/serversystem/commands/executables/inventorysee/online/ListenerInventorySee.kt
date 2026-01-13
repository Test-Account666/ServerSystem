package me.testaccount666.serversystem.commands.executables.inventorysee.online

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.executables.inventorysee.utils.AbstractInventorySeeListener
import me.testaccount666.serversystem.commands.executables.inventorysee.utils.InventorySeeUtils
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.Inventory

@RequiredCommands([CommandInventorySee::class])
class ListenerInventorySee : AbstractInventorySeeListener(), Listener {
    /**
     * Checks if the listener can be registered by finding the required CommandInventorySee instance.
     * 
     * @param commands Set of available commands
     * @return true if the listener can be registered, false otherwise
     */
    fun canRegister(commands: Set<ServerSystemCommandExecutor>): Boolean = internalCanRegister(commands)

    @EventHandler(priority = EventPriority.HIGH)
    fun onViewerClick(event: InventoryClickEvent) {
        val clickedInventory = event.clickedInventory ?: return

        val topInventory = event.view.topInventory
        val bottomInventory = event.view.bottomInventory
        val viewerPlayer = event.whoClicked as Player
        val owner = findInventoryOwner(topInventory, bottomInventory)

        if (owner == null || owner === viewerPlayer) return

        val cachedInventory = _commandInventorySee.inventoryCache[owner]
        if (cachedInventory == null || (cachedInventory !== topInventory && cachedInventory !== bottomInventory)) return

        if (!hasCommandPermission(viewerPlayer, "InventorySee.Modify", false)
            || (event.rawSlot in 45..<54)) event.isCancelled = true

        Bukkit.getScheduler().runTaskLater(instance, Runnable { _commandInventorySee.applyChangesToOwner(owner, cachedInventory) }, 1L)
    }

    @EventHandler(ignoreCancelled = true)
    fun onOwnerInventoryChange(event: PlayerDropItemEvent) = updateCachedInventory(event.getPlayer())

    @EventHandler(ignoreCancelled = true)
    fun onOwnerInventoryChange(event: PlayerPickupItemEvent) = updateCachedInventory(event.getPlayer())

    @EventHandler(ignoreCancelled = true)
    fun onOwnerInventoryChange(event: PlayerSwapHandItemsEvent) = updateCachedInventory(event.getPlayer())

    @EventHandler
    fun onGeneralClick(event: InventoryClickEvent) {
        val viewerPlayer = event.whoClicked as Player
        updateCachedInventory(viewerPlayer)
    }

    private fun findInventoryOwner(topInventory: Inventory, bottomInventory: Inventory): Player? {
        var holder = topInventory.holder
        if (holder is Player) return holder

        holder = bottomInventory.holder
        if (holder is Player) return holder

        return null
    }

    private fun updateCachedInventory(player: Player) {
        val cachedInventory = _commandInventorySee.inventoryCache[player] ?: return

        Bukkit.getScheduler().runTaskLater(instance, Runnable {
            _commandInventorySee.refreshInventoryContents(player, cachedInventory)
        }, 1L)
    }

    @EventHandler
    fun onViewedQuit(event: PlayerQuitEvent) {
        val player = event.getPlayer()

        val inventory = _commandInventorySee.inventoryCache[player] ?: return
        _commandInventorySee.inventoryCache.remove(player)

        InventorySeeUtils.handleInventoryViewers(inventory, player, 10L) { user, playerName ->
            _commandInventorySee.offlineInventorySee.processOfflineInventorySee(user, "", playerName)
        }
    }
}
