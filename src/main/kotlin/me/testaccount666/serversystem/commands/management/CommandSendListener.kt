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
        val sentCommands = event.commands.iterator()

        while (sentCommands.hasNext()) {
            val command = sentCommands.next()

            val pluginCommand = Bukkit.getPluginCommand(command) ?: continue
            if (pluginCommand.plugin !== instance) continue

            val executor = pluginCommand.executor
            val wrapper = executor as? CommandExecutorWrapper ?: continue
            val commandExecutor = wrapper.commandExecutor
            val serverCommand = commandExecutor as? AbstractServerSystemCommand ?: continue

            if (serverCommand.hasCommandAccess(event.getPlayer(), pluginCommand)) continue

            sentCommands.remove()
            log.log(Level.FINE, "Filtered command '${command}' from suggestions for player ${event.getPlayer().name}")
        }
    }
}
