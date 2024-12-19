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
    private final FileConfiguration _configurartion;
    private final File _warpFile = new File("plugins" + File.separator + "ServerSystem", "warps.yml");

    public WarpManager(ServerSystem plugin) {
        this._configurartion = YamlConfiguration.loadConfiguration(this._warpFile);
    }

    public void AddWarp(String name, Location location) {
        if (this.DoesWarpExist(name)) return;
        name = name.toLowerCase();
        var xCoordinate = location.getX();
        var yCoordinate = location.getY();
        var zCoordinate = location.getZ();
        double yaw = location.getYaw();
        double pitch = location.getPitch();

        this._configurartion.set("Warps." + name + ".X", xCoordinate);
        this._configurartion.set("Warps." + name + ".Y", yCoordinate);
        this._configurartion.set("Warps." + name + ".Z", zCoordinate);
        this._configurartion.set("Warps." + name + ".Yaw", yaw);
        this._configurartion.set("Warps." + name + ".Pitch", pitch);
        this._configurartion.set("Warps." + name + ".World", location.getWorld().getName());

        try {
            this._configurartion.save(this._warpFile);
            this._configurartion.load(this._warpFile);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    public boolean DoesWarpExist(String name) {
        name = name.toLowerCase();

        if (!this._warpFile.exists()) return false;

        if (!this._configurartion.isSet("Warps." + name)) return false;

        var worldName = this._configurartion.getString("Warps." + name + ".World");

        if (worldName == null) return false;

        var world = Bukkit.getWorld(worldName);

        return world != null;
    }

    public Location GetWarp(String name) {
        name = name.toLowerCase();
        if (!this.DoesWarpExist(name)) return null;

        var xCoordinate = this._configurartion.getDouble("Warps." + name + ".X");
        var yCoordinate = this._configurartion.getDouble("Warps." + name + ".Y");
        var zCoordinate = this._configurartion.getDouble("Warps." + name + ".Z");

        var yaw = this._configurartion.getDouble("Warps." + name + ".Yaw");
        var pitch = this._configurartion.getDouble("Warps." + name + ".Pitch");

        var worldName = this._configurartion.getString("Warps." + name + ".World");

        assert worldName != null;
        var world = Bukkit.getWorld(worldName);

        return new Location(world, xCoordinate, yCoordinate, zCoordinate, (float) yaw, (float) pitch);
    }

    public void DeleteWarp(String name) {
        name = name.toLowerCase();

        if (!this._warpFile.exists()) return;

        this._configurartion.set("Warps." + name, null);

        try {
            this._configurartion.save(this._warpFile);

            this._configurartion.load(this._warpFile);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    public List<String> GetWarps() {
        List<String> warps = new ArrayList<>();

        if (!this._warpFile.exists()) return warps;

        for (var warpName : this._configurartion.getConfigurationSection("Warps").getKeys(false)) {
            if (!this.DoesWarpExist(warpName)) continue;

            warps.add(warpName);
        }

        return warps;
    }
}
