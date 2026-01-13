package me.testaccount666.serversystem.commands.executables.enderchest

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.executables.enderchest.offline.CommandOfflineEnderChest
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.function.Consumer

@RequiredCommands([CommandEnderChest::class])
class ListenerEnderChest : Listener {
    private lateinit var _enderChest: CommandOfflineEnderChest

    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>): Boolean {
        var canRegister = false

        requiredCommands.forEach { command ->
            if (command !is CommandEnderChest) return@forEach
            command.offlineEnderChest.enderChestLoader ?: return@forEach

            _enderChest = command.offlineEnderChest
            canRegister = true
        }

        return canRegister
    }

    @EventHandler
    fun onViewedQuit(event: PlayerQuitEvent) {
        val viewedPlayer = event.getPlayer()
        val inventory = viewedPlayer.enderChest

        val viewers = ArrayList(inventory.viewers)
        inventory.close()

        Bukkit.getScheduler().runTaskLater(instance, Runnable {
            viewers.forEach(Consumer { viewer ->
                if (viewer !is Player) return@Consumer
                val cachedUser = instance.registry.getService<UserManager>().getUserOrNull(viewer) ?: return@Consumer

                if (cachedUser.isOfflineUser) return@Consumer
                val user = cachedUser.offlineUser as User
                _enderChest.executeEnderChestCommand(user, viewedPlayer.name)
            })
        }, 10L)
    }
}
