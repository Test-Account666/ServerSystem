package me.testaccount666.serversystem.commands.executables.enderchest.offline

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.executables.enderchest.CommandEnderChest
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerLoginEvent

@RequiredCommands([CommandEnderChest::class])
class ListenerOfflineEnderChest : Listener {
    private lateinit var _enderChestLoader: EnderChestLoader
    private lateinit var _enderChest: CommandEnderChest

    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>): Boolean {
        var canRegister = false

        requiredCommands.forEach { command ->
            _enderChest = command as? CommandEnderChest ?: return@forEach
            _enderChestLoader = command.offlineEnderChest.enderChestLoader ?: return@forEach

            canRegister = true
        }

        return canRegister
    }

    @EventHandler
    fun onViewedJoin(event: PlayerLoginEvent) {
        val viewedPlayer = event.getPlayer()

        val inventory = _enderChestLoader.inventoryMap.getValue(viewedPlayer.uniqueId)
        if (inventory == null) {
            log.fine("OfflineEnderChest: No offline inventory found for player ${viewedPlayer.name}")
            return
        }

        log.fine("OfflineEnderChest: Offline inventory was found for player ${viewedPlayer.name}")

        val viewers = ArrayList(inventory.viewers)
        inventory.close()

        _enderChestLoader.inventoryMap.removeByKey(viewedPlayer.uniqueId)
        _enderChestLoader.saveOfflineInventory(viewedPlayer.uniqueId, inventory)

        Bukkit.getScheduler().runTaskLater(instance, Runnable {
            viewers.forEach { viewer ->
                if (viewer !is Player) return@forEach
                val cachedUser = instance.registry.getService<UserManager>().getUserOrNull(viewer) ?: return@forEach

                if (cachedUser.isOfflineUser) return@forEach
                val user = cachedUser.offlineUser as User
                _enderChest.executeEnderChestCommand(user, viewedPlayer.name)
            }
        }, 10L)
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory

        if (!_enderChestLoader.inventoryMap.containsValue(inventory)) return

        val uuid = _enderChestLoader.inventoryMap.getKey(inventory) ?: return

        _enderChestLoader.inventoryMap.removeByValue(inventory)
        _enderChestLoader.saveOfflineInventory(uuid, inventory)
    }
}
