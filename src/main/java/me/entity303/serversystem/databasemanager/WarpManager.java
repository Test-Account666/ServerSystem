package me.entity303.serversystem.databasemanager;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WarpManager {
    private final FileConfiguration cfg;
    private final File warpFile = new File("plugins" + File.separator + "ServerSystem", "warps.yml");

    public WarpManager(ServerSystem plugin) {
        this.cfg = YamlConfiguration.loadConfiguration(this.warpFile);

        File legacyWarpsFile = new File("plugins//ServerSystem", "warps.h2.mv.db");

        if (!legacyWarpsFile.exists())
            return;

        plugin.log("Found legacy warp database!");
        plugin.log("Trying to convert...");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        try {
            FileUtils.copyFile(legacyWarpsFile, new File("plugins" + File.separator + "ServerSystem", legacyWarpsFile.getName() + ".backup-" + date));
        } catch (IOException e) {
            e.printStackTrace();

            plugin.error("Failed to convert legacy warp database!");
            return;
        }

        LegacyWarpManager legacyWarpManager = new LegacyWarpManager(plugin);

        try {
            List<String> warps = legacyWarpManager.getWarps();

            for (String warp : warps)
                this.addWarp(warp, legacyWarpManager.getWarp(warp));

            legacyWarpManager.close();

            legacyWarpsFile.delete();
        } catch (Throwable throwable) {
            throwable.printStackTrace();

            plugin.error("Failed to convert legacy warp database!");
            return;
        }

        plugin.log("Legacy warp database was successfully converted!");
    }

    public void addWarp(String name, Location location) {
        if (this.doesWarpExist(name)) return;
        name = name.toLowerCase();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
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

    public Location getWarp(String name) {
        name = name.toLowerCase();
        if (!this.doesWarpExist(name))
            return null;

        double x = this.cfg.getDouble("Warps." + name + ".X");
        double y = this.cfg.getDouble("Warps." + name + ".Y");
        double z = this.cfg.getDouble("Warps." + name + ".Z");

        double yaw = this.cfg.getDouble("Warps." + name + ".Yaw");
        double pitch = this.cfg.getDouble("Warps." + name + ".Pitch");

        String worldName = this.cfg.getString("Warps." + name + ".World");

        assert worldName != null;
        World world = Bukkit.getWorld(worldName);

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

    public boolean doesWarpExist(String name) {
        name = name.toLowerCase();

        if (!this.warpFile.exists())
            return false;

        if (!this.cfg.isSet("Warps." + name))
            return false;

        String worldName = this.cfg.getString("Warps." + name + ".World");

        if (worldName == null)
            return false;

        World world = Bukkit.getWorld(worldName);

        if (world == null)
            return false;

        return true;
    }

    public List<String> getWarps() {
        List<String> warps = new ArrayList<>();

        if (!this.warpFile.exists())
            return warps;

        for (String warpName : this.cfg.getConfigurationSection("Warps").getKeys(false)) {
            if (!this.doesWarpExist(warpName))
                continue;

            warps.add(warpName);
        }

        return warps;
    }
}
