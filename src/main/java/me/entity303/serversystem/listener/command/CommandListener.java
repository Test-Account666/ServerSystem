package me.entity303.serversystem.listener.command;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandListener extends CommandUtils implements Listener {

    public CommandListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void OnCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equalsIgnoreCase("/restart")) {
            if (this._plugin.getCommand("restart") == null)
                return;

            event.setCancelled(true);
            this._plugin.getCommand("restart").execute(event.getPlayer(), "restart", new String[] { });
        }

        for (var player : this._plugin.GetCommanddSpy()) {
            var commandLabel = event.getMessage();
            var command = event.getMessage();
            player.sendMessage(
                    this._plugin.GetMessages().GetMessage(commandLabel, command, event.getPlayer(), player, "CommandSpy.Spy").replace("<MESSAGE>", event.getMessage()));
        }
    }

    @EventHandler
    public void OnServerCmd(ServerCommandEvent event) {
        this.SendCommandSpyMessages(event);
    }

    private void SendCommandSpyMessages(ServerCommandEvent event) {
        if (Bukkit.getOnlinePlayers().isEmpty())
            return;

        Bukkit.getOnlinePlayers().forEach(all -> {
            if (this._plugin.GetCommanddSpy().contains(all)) {
                var commandLabel = event.getCommand();
                var command = event.getCommand();
                var sender = event.getSender();
                all.sendMessage(this._plugin.GetMessages().GetMessage(commandLabel, command, sender, all, "CommandSpy.Spy").replace("<MESSAGE>", event.getCommand()));
            }
        });
    }

    @EventHandler
    public void OnRemoteServerCmd(RemoteServerCommandEvent event) {
        this.SendCommandSpyMessages(event);
    }
}
