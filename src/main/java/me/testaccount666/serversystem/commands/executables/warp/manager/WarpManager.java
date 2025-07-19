package me.testaccount666.serversystem.commands.executables.warp.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class WarpManager {
    private final Set<Warp> _warps = new HashSet<>();
    private final FileConfiguration _config;
    private final File _file;

    /**
     * Creates a new WarpManager.
     *
     * @param config The warp configuration
     * @param file   The warp configuration file
     */
    public WarpManager(FileConfiguration config, File file) {
        _config = config;
        _file = file;

        loadWarps();
    }

    /**
     * Gets all warp.
     *
     * @return An unmodifiable set of all warps
     */
    public Set<Warp> getWarps() {
        return Collections.unmodifiableSet(_warps);
    }

    /**
     * Adds a new warp with the specified name and location.
     * The warp will be saved to the configuration file.
     *
     * @param name     The name of the warp
     * @param location The location of the warp
     */
    public Warp addWarp(String name, Location location) {
        return addWarp(name, location, true);
    }

    /**
     * Adds a new warp with the specified name and location.
     *
     * @param name      The name of the warp
     * @param location  The location of the warp
     * @param saveWarps Whether to save the warp to the configuration file
     */
    public Warp addWarp(String name, Location location, boolean saveWarps) {
        return addWarp(new Warp(name, location), saveWarps);
    }

    /**
     * Adds the specified warp.
     * The warp will be saved to the configuration file.
     *
     * @param warp The warp to add
     */
    public void addWarp(Warp warp) {
        addWarp(warp, true);
    }

    /**
     * Adds the specified warp.
     *
     * @param warp      The warp to add
     * @param saveWarps Whether to save the warp to the configuration file
     */
    public Warp addWarp(Warp warp, boolean saveWarps) {
        _warps.add(warp);

        if (saveWarps) saveWarps();
        return warp;
    }

    /**
     * Removes the warp with the specified name.
     * The change will be saved to the configuration file.
     *
     * @param name The name of the warp to remove
     */
    public void removeWarp(String name) {
        _warps.removeIf(warp -> warp.getName().equalsIgnoreCase(name));

        saveWarps();
    }

    public void removeWarp(Warp warp) {
        _warps.removeIf(savedWarp -> savedWarp.getName().equalsIgnoreCase(warp.getName()));

        saveWarps();
    }

    /**
     * Gets the warp with the specified name.
     *
     * @param name The name of the warp to get
     * @return An Optional containing the warp, or an empty Optional if no warp with the specified name exists
     */
    public Optional<Warp> getWarpByName(String name) {
        return _warps.stream()
                .filter(warp -> warp.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Checks if a warp with the specified name exists.
     *
     * @param name The name of the warp to check for
     * @return true if a warp with the specified name exists, false otherwise
     */
    public boolean warpExists(String name) {
        return getWarpByName(name).isPresent();
    }


    private void saveWarps() {
        _config.set("Warps", null);

        for (var warp : _warps) {
            var prefix = "Warps.${warp.getName()}";

            _config.set("${prefix}.X", warp.getLocation().getX());
            _config.set("${prefix}.Y", warp.getLocation().getY());
            _config.set("${prefix}.Z", warp.getLocation().getZ());

            _config.set("${prefix}.Yaw", warp.getLocation().getYaw());
            _config.set("${prefix}.Pitch", warp.getLocation().getPitch());

            _config.set("${prefix}.World", warp.getLocation().getWorld().getName());
        }

        try {
            _config.save(_file);
        } catch (IOException exception) {
            throw new RuntimeException("Error saving warps file", exception);
        }
    }

    private void loadWarps() {
        _warps.clear();

        if (!_config.isConfigurationSection("Warps")) return;

        var warpNames = _config.getConfigurationSection("Warps").getKeys(false);

        for (var name : warpNames) {
            var prefix = "Warps.${name}";

            var warp = parseWarp(name, prefix);

            if (warp.isEmpty()) continue;

            _warps.add(warp.get());
        }
    }

    private Optional<Warp> parseWarp(String name, String prefix) {
        var x = _config.getDouble("${prefix}.X");
        var y = _config.getDouble("${prefix}.Y");
        var z = _config.getDouble("${prefix}.Z");

        var yaw = (float) _config.getDouble("${prefix}.Yaw");
        var pitch = (float) _config.getDouble("${prefix}.Pitch");

        var worldName = _config.getString("${prefix}.World", "");
        var world = Bukkit.getWorld(worldName);

        if (world == null) return Optional.empty();

        var location = new Location(world, x, y, z, yaw, pitch);

        return Optional.of(new Warp(name, location));
    }
}
