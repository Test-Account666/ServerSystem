package me.testaccount666.serversystem.commands.executables.back

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerTeleportEvent

@RequiredCommands([CommandBack::class])
class ListenerBack : Listener {
    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>): Boolean = requiredCommands.any { it is CommandBack }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val user = instance.registry.getService<UserManager>().getUserOrNull(event.getPlayer()) ?: return

        val offlineUser = user.offlineUser

        offlineUser.lastTeleportLocation = event.from
        offlineUser.lastBackType = CommandBack.BackType.TELEPORT
        offlineUser.save()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val user = instance.registry.getService<UserManager>().getUserOrNull(event.getEntity()) ?: return

        val offlineUser = user.offlineUser

        offlineUser.lastDeathLocation = event.player.location
        offlineUser.lastBackType = CommandBack.BackType.DEATH
        offlineUser.save()
    }
}
