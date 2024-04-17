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
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().equalsIgnoreCase("/restart")) {
            if (this.plugin.getCommand("restart") == null)
                return;

            e.setCancelled(true);
            this.plugin.getCommand("restart").execute(e.getPlayer(), "restart", new String[] { });
        }

        for (var player : this.plugin.getCmdSpy()) {
            var label = e.getMessage();
            var command = e.getMessage();
            player.sendMessage(
                    this.plugin.getMessages().getMessage(label, command, e.getPlayer(), player, "CommandSpy.Spy").replace("<MESSAGE>", e.getMessage()));
        }
    }

    @EventHandler
    public void onServerCmd(ServerCommandEvent e) {
        this.SendCommandSpyMessages(e);
    }

    private void SendCommandSpyMessages(ServerCommandEvent e) {
        if (Bukkit.getOnlinePlayers().isEmpty())
            return;

        Bukkit.getOnlinePlayers().forEach(all -> {
            if (this.plugin.getCmdSpy().contains(all)) {
                var label = e.getCommand();
                var command = e.getCommand();
                var sender = e.getSender();
                all.sendMessage(this.plugin.getMessages().getMessage(label, command, sender, all, "CommandSpy.Spy").replace("<MESSAGE>", e.getCommand()));
            }
        });
    }

    @EventHandler
    public void onRemoteServerCmd(RemoteServerCommandEvent e) {
        this.SendCommandSpyMessages(e);
    }
}
