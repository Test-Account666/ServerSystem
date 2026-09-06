package me.testaccount666.serversystem.commands.executables.back

import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.event.*
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerTeleportEvent

@RequiredCommands([CommandBack::class])
class ListenerBack : Listener {
    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>) = requiredCommands.any { it is CommandBack }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val user = getService<UserManager>().getUserOrNull(event.getPlayer())?.offlineUser ?: return

        user.lastTeleportLocation = event.from
        user.lastBackType = CommandBack.BackType.TELEPORT
        user.save()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val user = getService<UserManager>().getUserOrNull(event.getEntity())?.offlineUser ?: return

        user.lastDeathLocation = event.player.location
        user.lastBackType = CommandBack.BackType.DEATH
        user.save()
    }
}
