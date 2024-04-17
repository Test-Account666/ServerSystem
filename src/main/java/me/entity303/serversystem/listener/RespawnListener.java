package me.entity303.serversystem.listener;

import me.entity303.serversystem.commands.executable.SpawnCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.io.File;

public class RespawnListener extends CommandUtils implements Listener {

    public RespawnListener(ServerSystem plugin) {
        super(plugin);
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (!e.isBedSpawn() || this.plugin.getConfigReader().getBoolean("spawn.forceRespawn")) {
            var spawnFile = new File("plugins//ServerSystem", "spawn.yml");
            if (!spawnFile.exists()) {
                var sender = e.getPlayer().getName();
                e.getPlayer()
                 .sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("spawn", "spawn", sender, null, "Spawn.NoSpawn"));
            } else {
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(spawnFile);
                if (Bukkit.getWorld(cfg.getString("Spawn.World")) == null) {
                    var sender = e.getPlayer().getName();
                    e.getPlayer()
                     .sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("spawn", "spawn", sender, null, "Spawn.NoSpawn"));
                }
                var location = e.getPlayer().getLocation().clone();
                SpawnCommand.GetSpawnLocation(cfg, location);
                e.setRespawnLocation(location);
            }
        }
    }
}
