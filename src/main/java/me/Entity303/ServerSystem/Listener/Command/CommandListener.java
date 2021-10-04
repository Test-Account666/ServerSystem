package me.Entity303.ServerSystem.Listener.Command;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandListener extends ServerSystemCommand implements Listener {

    public CommandListener(ss plugin) {
        super(plugin);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().equalsIgnoreCase("/restart")) {
            e.setCancelled(true);
            this.plugin.getCommand("restart").execute(e.getPlayer(), "restart", new String[]{});
        }
        for (Player player : this.plugin.getCmdSpy())
            player.sendMessage(this.getMessage("CommandSpy.Spy", e.getMessage(), e.getMessage(), e.getPlayer(), player).replace("<MESSAGE>", e.getMessage()));
    }

    @EventHandler
    public void onServerCmd(ServerCommandEvent e) {
        if (Bukkit.getOnlinePlayers().size() > 0) Bukkit.getOnlinePlayers().forEach(all -> {
            if (this.plugin.getCmdSpy().contains(all))
                all.sendMessage(this.getMessage("CommandSpy.Spy", e.getCommand(), e.getCommand(), e.getSender(), all).replace("<MESSAGE>", e.getCommand()));
        });
    }

    @EventHandler
    public void onRemoteServerCmd(RemoteServerCommandEvent e) {
        if (Bukkit.getOnlinePlayers().size() > 0) Bukkit.getOnlinePlayers().forEach(all -> {
            if (this.plugin.getCmdSpy().contains(all))
                all.sendMessage(this.getMessage("CommandSpy.Spy", e.getCommand(), e.getCommand(), e.getSender(), all).replace("<MESSAGE>", e.getCommand()));
        });
    }
}
