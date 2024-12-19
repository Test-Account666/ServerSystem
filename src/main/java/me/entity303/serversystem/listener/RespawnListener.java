package me.entity303.serversystem.listener;

import me.entity303.serversystem.commands.executable.SpawnCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.io.File;

public class RespawnListener implements Listener {

    protected final ServerSystem _plugin;

    public RespawnListener(ServerSystem plugin) {
        this._plugin = plugin;
    }


    @EventHandler
    public void OnRespawn(PlayerRespawnEvent event) {
        if (!event.isBedSpawn() || this._plugin.GetConfigReader().GetBoolean("spawn.forceRespawn")) {
            var spawnFile = new File("plugins//ServerSystem", "spawn.yml");
            if (!spawnFile.exists()) {
                var sender = event.getPlayer().getName();
                event.getPlayer()
                     .sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage("spawn", "spawn", sender, null, "Spawn.NoSpawn"));
            } else {
                var cfg = YamlConfiguration.loadConfiguration(spawnFile);
                if (Bukkit.getWorld(cfg.getString("Spawn.World")) == null) {
                    var sender = event.getPlayer().getName();
                    event.getPlayer()
                         .sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage("spawn", "spawn", sender, null, "Spawn.NoSpawn"));
                }
                var location = event.getPlayer().getLocation().clone();
                SpawnCommand.GetSpawnLocation(cfg, location);
                event.setRespawnLocation(location);
            }
        }
    }
}
