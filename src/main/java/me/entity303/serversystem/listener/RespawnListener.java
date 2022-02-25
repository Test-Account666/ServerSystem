package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.io.File;

public class RespawnListener extends MessageUtils implements Listener {

    public RespawnListener(ServerSystem plugin) {
        super(plugin);
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (!e.isBedSpawn() || this.plugin.getConfigReader().getBoolean("spawn.forceRespawn")) {
            File spawnFile = new File("plugins//ServerSystem", "spawn.yml");
            if (!spawnFile.exists())
                e.getPlayer().sendMessage(this.getPrefix() + this.getMessage("Spawn.NoSpawn", "spawn", "spawn", e.getPlayer().getName(), null));
            else {
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(spawnFile);
                if (Bukkit.getWorld(cfg.getString("Spawn.World")) == null)
                    e.getPlayer().sendMessage(this.getPrefix() + this.getMessage("Spawn.NoSpawn", "spawn", "spawn", e.getPlayer().getName(), null));
                Location location = e.getPlayer().getLocation().clone();
                location.setX(cfg.getDouble("Spawn.X"));
                location.setY(cfg.getDouble("Spawn.Y"));
                location.setZ(cfg.getDouble("Spawn.Z"));
                location.setYaw((float) cfg.getDouble("Spawn.Yaw"));
                location.setPitch((float) cfg.getDouble("Spawn.Pitch"));
                location.setWorld(Bukkit.getWorld(cfg.getString("Spawn.World")));
                e.setRespawnLocation(location);
            }
        }
    }
}
