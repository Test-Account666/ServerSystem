package me.testaccount666.serversystem.commands.executables.spawn

import me.testaccount666.paperktx.scheduler.skedule.okkero.schedule
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.UserManager
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

        instance.schedule {
            waitFor(20L)
            val cachedUser = getService<UserManager>().getUserOrNull(player) ?: return@schedule
            if (cachedUser.isOfflineUser) return@schedule

            _commandSpawn.handleSpawnCommand(cachedUser.onlineUser, "spawn", false)
        }
    }
}
