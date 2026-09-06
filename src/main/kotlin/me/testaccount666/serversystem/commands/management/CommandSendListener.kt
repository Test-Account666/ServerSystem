package me.testaccount666.serversystem.commands.management

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.wrappers.CommandExecutorWrapper
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandSendEvent
import java.util.logging.Level

/**
 * Listens for ServerSystem command suggestions being sent
 * to players and filters out commands
 * that the player doesn't have permission to use.
 */
class CommandSendListener : Listener {
    @EventHandler
    fun onCommandSend(event: PlayerCommandSendEvent) {
        event.commands.removeIf { name ->
            val pluginCommand = Bukkit.getPluginCommand(name)?.takeIf { it.plugin === instance } ?: return@removeIf false

            val serverCommand = (
                    pluginCommand.executor as? CommandExecutorWrapper
                    )?.commandExecutor as? AbstractServerSystemCommand ?: return@removeIf false

            if (serverCommand.hasCommandAccess(event.getPlayer(), pluginCommand)) return@removeIf false

            log.log(Level.FINE, "Filtered command '${name}' from suggestions for player ${event.getPlayer().name}")
            return@removeIf true
        }
    }
}
