package me.testaccount666.serversystem.commands.executables.inventorysee.offline

import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.executables.inventorysee.online.CommandInventorySee
import me.testaccount666.serversystem.commands.executables.inventorysee.utils.AbstractInventorySeeListener
import me.testaccount666.serversystem.commands.executables.inventorysee.utils.InventorySeeUtils
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerLoginEvent

@RequiredCommands([CommandInventorySee::class])
class ListenerOfflineInventorySee : AbstractInventorySeeListener(), Listener {
    /**
     * Checks if the listener can be registered by finding the required CommandInventorySee instance.
     * 
     * @param commands Set of available commands
     * @return true if the listener can be registered, false otherwise
     */
    fun canRegister(commands: Set<ServerSystemCommandExecutor>): Boolean = internalCanRegister(commands)

    override fun additionalRegistrationChecks(): Boolean = _commandInventorySee.offlineInventorySee.inventoryLoader != null

    @EventHandler
    fun onPlayerJoin(event: PlayerLoginEvent) { // Sorry paper, but I think PlayerJoinEvent is too late.
        val inventoryLoader = _commandInventorySee.offlineInventorySee.inventoryLoader
        val player = event.getPlayer()
        val uuid = player.uniqueId

        // Nullability can be safely ignored, because inventoryLoader cannot be null, if this listener is registered.
        val inventoryMap = inventoryLoader!!.inventoryMap

        val inventory = inventoryMap.getValue(uuid) ?: return

        inventoryMap.removeByKey(uuid)
        inventoryLoader.saveOfflineInventory(uuid, inventory)

        InventorySeeUtils.handleInventoryViewers(
            inventory, player, 10L
        ) { user, playerName -> _commandInventorySee.processInventorySee(user, "", playerName) }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventoryLoader = _commandInventorySee.offlineInventorySee.inventoryLoader
        val inventory = event.inventory

        // Nullability can be safely ignored, because inventoryLoader cannot be null, if this listener is registered.
        val inventoryMap = inventoryLoader!!.inventoryMap
        val uuid = inventoryMap.getKey(inventory) ?: return

        inventoryMap.removeByKey(uuid)
        inventoryLoader.saveOfflineInventory(uuid, inventory)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val inventoryLoader = _commandInventorySee.offlineInventorySee.inventoryLoader!!
        val inventory = event.inventory

        val inventoryMap = inventoryLoader.inventoryMap
        if (!inventoryMap.containsValue(inventory)) return

        if (event.rawSlot !in 41..<54) return
        event.isCancelled = true
    }
}
