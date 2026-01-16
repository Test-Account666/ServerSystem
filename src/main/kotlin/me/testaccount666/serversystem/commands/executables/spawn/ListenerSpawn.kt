package me.testaccount666.serversystem.commands.executables.spawn

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@RequiredCommands([CommandSpawn::class])
class ListenerSpawn : Listener {
    private lateinit var _commandSpawn: CommandSpawn

    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>): Boolean {
        _commandSpawn = requiredCommands.firstOrNull { it is CommandSpawn } as? CommandSpawn ?: return false
        return true
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.getPlayer()

        if (player.hasPlayedBefore()) {
            if (!_commandSpawn.teleportOnJoin) return
        } else if (!_commandSpawn.teleportOnFirstJoin) return

        // Delay by a second, because teleporting instantly sometimes doesn't work
        Bukkit.getScheduler().runTaskLater(instance, Runnable {
            val cachedUser = instance.registry.getService<UserManager>().getUserOrNull(player) ?: return@Runnable
            if (cachedUser.isOfflineUser) return@Runnable

            val user = cachedUser.offlineUser as User
            _commandSpawn.handleSpawnCommand(user, "spawn")
        }, 20L)
    }
}
