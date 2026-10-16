package me.testaccount666.serversystem.commands.executables.enderchest.offline

import me.testaccount666.paperktx.scheduler.skedule.okkero.schedule
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.executables.enderchest.online.CommandEnderChest
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.UserManager
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

        requiredCommands.forEach {
            _enderChest = it as? CommandEnderChest ?: return@forEach
            _enderChestLoader = it.offlineEnderChest.enderChestLoader ?: return@forEach

            canRegister = true
        }

        return canRegister
    }

    @EventHandler
    fun onViewedJoin(event: PlayerLoginEvent) {
        val viewedPlayer = event.getPlayer()

        val inventory = _enderChestLoader.inventoryMap.getValue(viewedPlayer.uniqueId) ?: run {
            log.fine("OfflineEnderChest: No offline inventory found for player ${viewedPlayer.name}")
            return
        }

        log.fine("OfflineEnderChest: Offline inventory was found for player ${viewedPlayer.name}")

        val viewers = ArrayList(inventory.viewers)
        inventory.close()

        _enderChestLoader.inventoryMap.removeByKey(viewedPlayer.uniqueId)
        _enderChestLoader.saveOfflineInventory(viewedPlayer.uniqueId, inventory)

        instance.schedule {
            waitFor(10L)
            viewers.filterIsInstance<Player>().forEach { viewer ->
                val cachedUser = getService<UserManager>().getUserOrNull(viewer) ?: return@forEach

                if (cachedUser.isOfflineUser) return@forEach
                val user = cachedUser.onlineUser
                _enderChest.executeEnderChestCommand(user, viewedPlayer.name)
            }
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory
        val uuid = _enderChestLoader.inventoryMap.getKey(inventory) ?: return

        _enderChestLoader.inventoryMap.removeByValue(inventory)
        _enderChestLoader.saveOfflineInventory(uuid, inventory)
    }
}
