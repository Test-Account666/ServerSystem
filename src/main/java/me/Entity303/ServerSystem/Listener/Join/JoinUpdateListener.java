package me.Entity303.ServerSystem.Listener.Join;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinUpdateListener implements Listener {
    private final ss plugin;

    public JoinUpdateListener(ss plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (this.plugin.getPermissions().hasPerm(e.getPlayer(), "updatenotify", true))
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                if (this.plugin.getVersionManager().isV116())
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + e.getPlayer().getName() + " [\"\",{\"text\":\"-----------------------------------------------------\",\"color\":\"#BB0000\"},{\"text\":\"\\n\"},{\"text\":\"ServerSystem\",\"color\":\"#8A950B\"},{\"text\":\"\\n\"},{\"text\":\"-----------------------------------------------------\",\"color\":\"#BB0000\"},{\"text\":\"\\n\"},{\"text\":\"Update Needed (New version: " + this.plugin.getNewVersion() + ")!\",\"color\":\"#FF8000\"},{\"text\":\"\\n\"},{\"text\":\"Download here: https://www.spigotmc.org/resources/serversystem.78974/\",\"color\":\"#FF8000\"},{\"text\":\"\\n\"},{\"text\":\"-----------------------------------------------------\",\"color\":\"#BB0000\"},{\"text\":\"\\n\"},{\"text\":\"ServerSystem\",\"color\":\"#8A950B\"},{\"text\":\"\\n\"},{\"text\":\"-----------------------------------------------------\",\"color\":\"#BB0000\"}]");
                else {
                    e.getPlayer().sendMessage("§c-----------------------------------------------------");
                    e.getPlayer().sendMessage("§aServerSystem");
                    e.getPlayer().sendMessage("§c-----------------------------------------------------");
                    e.getPlayer().sendMessage("§2Update Needed (New version: " + this.plugin.getNewVersion() + ")!");
                    e.getPlayer().sendMessage("§2Download here: https://www.spigotmc.org/resources/serversystem.78974/");
                    e.getPlayer().sendMessage("§c-----------------------------------------------------");
                    e.getPlayer().sendMessage("§aServerSystem");
                    e.getPlayer().sendMessage("§c-----------------------------------------------------");
                }
            }, 20L);
    }
}
