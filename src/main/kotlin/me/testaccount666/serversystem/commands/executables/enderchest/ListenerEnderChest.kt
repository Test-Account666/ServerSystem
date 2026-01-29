package me.testaccount666.serversystem.commands.executables.enderchest

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.executables.enderchest.offline.CommandOfflineEnderChest
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.ServiceExtensions.getService
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

@RequiredCommands([CommandEnderChest::class])
class ListenerEnderChest : Listener {
    private lateinit var _enderChest: CommandOfflineEnderChest

    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>): Boolean {
        var canRegister = false

        requiredCommands.forEach {
            if (it !is CommandEnderChest) return@forEach
            it.offlineEnderChest.enderChestLoader ?: return@forEach

            _enderChest = it.offlineEnderChest
            canRegister = true
        }

        return canRegister
    }

    @EventHandler
    fun onViewedQuit(event: PlayerQuitEvent) {
        val viewedPlayer = event.player
        val inventory = viewedPlayer.enderChest

        val viewers = inventory.viewers.toList()
        inventory.close()

        Bukkit.getScheduler().runTaskLater(instance, Runnable {
            viewers.filterIsInstance<Player>().forEach { viewer ->
                val cachedUser = getService<UserManager>().getUserOrNull(viewer) ?: return@forEach

                if (cachedUser.isOfflineUser) return@forEach
                val user = cachedUser.offlineUser as User
                _enderChest.executeEnderChestCommand(user, viewedPlayer.name)
            }
        }, 10L)
    }
}
