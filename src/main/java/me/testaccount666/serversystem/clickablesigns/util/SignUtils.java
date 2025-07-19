package me.testaccount666.serversystem.clickablesigns.util;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Path;

/**
 * Utility class for common sign operations.
 */
public class SignUtils {
    private static final Path _DATA_DIRECTORY_PATH = Path.of("plugins", "ServerSystem", "data", "signs");

    /**
     * Converts a location to a string format used for file names.
     *
     * @param location The location to convert
     * @return The location as a string in the format "world_x_y_z"
     */
    public static String locationToString(Location location) {
        return "${location.getWorld().getName()}_${location.getBlockX()}_${location.getBlockY()}_${location.getBlockZ()}";
    }

    /**
     * Gets the data directory for sign configurations.
     * Creates the directory if it doesn't exist.
     *
     * @return The data directory
     */
    public static File getDataDirectory() {
        var dataDirectory = _DATA_DIRECTORY_PATH.toFile();
        if (!dataDirectory.exists()) dataDirectory.mkdirs();
        return dataDirectory;
    }

    /**
     * Gets the configuration file for a sign at the specified location.
     *
     * @param location The location of the sign
     * @return The configuration file
     */
    public static File getSignFile(Location location) {
        getDataDirectory();
        return _DATA_DIRECTORY_PATH.resolve(locationToString(location) + ".yml").toFile();
    }

    /**
     * Loads the configuration for a sign at the specified location.
     *
     * @param location The location of the sign
     * @return The configuration
     */
    public static FileConfiguration loadSignConfig(Location location) {
        return YamlConfiguration.loadConfiguration(getSignFile(location));
    }
}