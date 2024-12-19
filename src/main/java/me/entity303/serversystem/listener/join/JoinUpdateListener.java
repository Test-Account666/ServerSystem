package me.entity303.serversystem.listener.join;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinUpdateListener implements Listener {
    private final ServerSystem _plugin;

    public JoinUpdateListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnJoin(PlayerJoinEvent event) {
        if (this._plugin.GetPermissions().HasPermission(event.getPlayer(), "updatenotify", true)) {
            Bukkit.getScheduler()
                  .runTaskLater(this._plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + event.getPlayer().getName() + " [\"\"," + "{\"text" +
                                                                                                      "\":\"-----------------------------------------------------\",\"color\":\"#BB0000\"},{\"text\":\"\\n\"},{\"text\":\"ServerSystem\",\"color\":\"#8A950B\"},{\"text\":\"\\n\"},{\"text\":\"-----------------------------------------------------\",\"color\":\"#BB0000\"},{\"text\":\"\\n\"},{\"text\":\"Update Needed (New version: " +
                                                                                                      this._plugin.GetNewVersion() + ")!\",\"color\":\"#FF8000\"}," +
                                                                                                      "{\"text\":\"\\n\"},{\"text\":\"Download " +
                                                                                                      "here: https://www.spigotmc" +
                                                                                                      ".org/resources/serversystem.78974/\"," +
                                                                                                      "\"color\":\"#FF8000\"}," + "{\"text\":\"\\n\"}," + "{\"text" +
                                                                                                      "\":\"-----------------------------------------------------\",\"color\":\"#BB0000\"},{\"text\":\"\\n\"},{\"text\":\"ServerSystem\",\"color\":\"#8A950B\"},{\"text\":\"\\n\"},{\"text\":\"-----------------------------------------------------\",\"color\":\"#BB0000\"}]"),
                                20L);
        }
    }
}
