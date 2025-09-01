package me.testaccount666.serversystem.commands.management;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.commands.wrappers.CommandExecutorWrapper;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.logging.Level;

/**
 * Listens for ServerSystem command suggestions being sent
 * to players and filters out commands
 * that the player doesn't have permission to use.
 */
public class CommandSendListener implements Listener {

    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent event) {
        var sentCommands = event.getCommands().iterator();

        while (sentCommands.hasNext()) {
            var command = sentCommands.next();

            var pluginCommand = Bukkit.getPluginCommand(command);
            if (pluginCommand == null) continue;
            if (pluginCommand.getPlugin() != ServerSystem.Instance) continue;

            var executor = pluginCommand.getExecutor();
            if (!(executor instanceof CommandExecutorWrapper commandWrapper)) continue;
            if (!(commandWrapper.getCommandExecutor() instanceof AbstractServerSystemCommand commandExecutor)) continue;
            if (commandExecutor.hasCommandAccess(event.getPlayer(), pluginCommand)) continue;

            sentCommands.remove();
            ServerSystem.getLog().log(Level.FINE, "Filtered command '${command}' from suggestions for player ${event.getPlayer().getName()}");
        }
    }
}
