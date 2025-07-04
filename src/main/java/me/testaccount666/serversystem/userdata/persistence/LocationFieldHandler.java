package me.testaccount666.serversystem.userdata.persistence;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * A field handler for Bukkit Location objects.
 * This handler saves and loads locations with their world, coordinates, and rotation.
 */
public class LocationFieldHandler implements FieldHandler<Location> {

    @Override
    public void save(FileConfiguration config, String path, Location location) {
        if (location == null) {
            config.set(path, null);
            return;
        }

        config.set("${path}.World", location.getWorld().getName());
        config.set("${path}.X", location.getX());
        config.set("${path}.Y", location.getY());
        config.set("${path}.Z", location.getZ());
        config.set("${path}.Yaw", location.getYaw());
        config.set("${path}.Pitch", location.getPitch());
    }

    @Override
    public Location load(FileConfiguration config, String path, Location defaultValue) {
        if (!config.isSet(path)) return defaultValue;

        var worldName = config.getString("${path}.World", "");

        var world = Bukkit.getWorld(worldName);
        if (world == null) return defaultValue;

        var x = config.getDouble("${path}.X");
        var y = config.getDouble("${path}.Y");
        var z = config.getDouble("${path}.Z");
        var yaw = (float) config.getDouble("${path}.Yaw");
        var pitch = (float) config.getDouble("${path}.Pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }
}