package me.Entity303.ServerSystem.Listener;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.io.File;

public class RespawnListener extends ServerSystemCommand implements Listener {

    public RespawnListener(ss plugin) {
        super(plugin);
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (!e.isBedSpawn() || this.plugin.getConfig().getBoolean("spawn.forceRespawn")) {
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
