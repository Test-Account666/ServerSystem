package me.entity303.serversystem.listener.join;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinUpdateListener implements Listener {
    private final ServerSystem plugin;

    public JoinUpdateListener(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (this.plugin.getPermissions().hasPermission(e.getPlayer(), "updatenotify", true))
            Bukkit.getScheduler()
                  .runTaskLater(this.plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + e.getPlayer().getName() +
                                                                                                     " [\"\",{\"text\":\"-----------------------------------------------------\",\"color\":\"#BB0000\"},{\"text\":\"\\n\"},{\"text\":\"ServerSystem\",\"color\":\"#8A950B\"},{\"text\":\"\\n\"},{\"text\":\"-----------------------------------------------------\",\"color\":\"#BB0000\"},{\"text\":\"\\n\"},{\"text\":\"Update Needed (New version: " +
                                                                                                     this.plugin.getNewVersion() +
                                                                                                     ")!\",\"color\":\"#FF8000\"},{\"text\":\"\\n\"},{\"text\":\"Download here: https://www.spigotmc.org/resources/serversystem.78974/\",\"color\":\"#FF8000\"},{\"text\":\"\\n\"},{\"text\":\"-----------------------------------------------------\",\"color\":\"#BB0000\"},{\"text\":\"\\n\"},{\"text\":\"ServerSystem\",\"color\":\"#8A950B\"},{\"text\":\"\\n\"},{\"text\":\"-----------------------------------------------------\",\"color\":\"#BB0000\"}]"),
                                20L);
    }
}
