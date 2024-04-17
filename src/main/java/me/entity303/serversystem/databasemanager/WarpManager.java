package me.entity303.serversystem.databasemanager;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WarpManager {
    private final FileConfiguration cfg;
    private final File warpFile = new File("plugins" + File.separator + "ServerSystem", "warps.yml");

    public WarpManager(ServerSystem plugin) {
        this.cfg = YamlConfiguration.loadConfiguration(this.warpFile);
    }

    public void addWarp(String name, Location location) {
        if (this.doesWarpExist(name))
            return;
        name = name.toLowerCase();
        var x = location.getX();
        var y = location.getY();
        var z = location.getZ();
        double yaw = location.getYaw();
        double pitch = location.getPitch();

        this.cfg.set("Warps." + name + ".X", x);
        this.cfg.set("Warps." + name + ".Y", y);
        this.cfg.set("Warps." + name + ".Z", z);
        this.cfg.set("Warps." + name + ".Yaw", yaw);
        this.cfg.set("Warps." + name + ".Pitch", pitch);
        this.cfg.set("Warps." + name + ".World", location.getWorld().getName());

        try {
            this.cfg.save(this.warpFile);
            this.cfg.load(this.warpFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public boolean doesWarpExist(String name) {
        name = name.toLowerCase();

        if (!this.warpFile.exists())
            return false;

        if (!this.cfg.isSet("Warps." + name))
            return false;

        var worldName = this.cfg.getString("Warps." + name + ".World");

        if (worldName == null)
            return false;

        var world = Bukkit.getWorld(worldName);

        return world != null;
    }

    public Location getWarp(String name) {
        name = name.toLowerCase();
        if (!this.doesWarpExist(name))
            return null;

        var x = this.cfg.getDouble("Warps." + name + ".X");
        var y = this.cfg.getDouble("Warps." + name + ".Y");
        var z = this.cfg.getDouble("Warps." + name + ".Z");

        var yaw = this.cfg.getDouble("Warps." + name + ".Yaw");
        var pitch = this.cfg.getDouble("Warps." + name + ".Pitch");

        var worldName = this.cfg.getString("Warps." + name + ".World");

        assert worldName != null;
        var world = Bukkit.getWorld(worldName);

        return new Location(world, x, y, z, (float) yaw, (float) pitch);
    }

    public void deleteWarp(String name) {
        name = name.toLowerCase();

        if (!this.warpFile.exists())
            return;

        this.cfg.set("Warps." + name, null);

        try {
            this.cfg.save(this.warpFile);

            this.cfg.load(this.warpFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public List<String> getWarps() {
        List<String> warps = new ArrayList<>();

        if (!this.warpFile.exists())
            return warps;

        for (var warpName : this.cfg.getConfigurationSection("Warps").getKeys(false)) {
            if (!this.doesWarpExist(warpName))
                continue;

            warps.add(warpName);
        }

        return warps;
    }
}
