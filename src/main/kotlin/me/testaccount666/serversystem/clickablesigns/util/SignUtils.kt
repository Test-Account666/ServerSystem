package me.testaccount666.serversystem.clickablesigns.util

import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Path

/**
 * Utility class for common sign operations.
 */
object SignUtils {
    private val _DATA_DIRECTORY_PATH: Path = Path.of("plugins", "ServerSystem", "data", "signs")

    /**
     * Converts a location to a string format used for file names.
     * 
     * @param location The location to convert
     * @return The location as a string in the format "world_x_y_z"
     */
    fun locationToString(location: Location): String = "${location.world.name}_${location.blockX}_${location.blockY}_${location.blockZ}"

    val dataDirectory: File
        /**
         * Gets the data directory for sign configurations.
         * Creates the directory if it doesn't exist.
         * 
         * @return The data directory
         */
        get() {
            val dataDirectory = _DATA_DIRECTORY_PATH.toFile()
            if (!dataDirectory.exists()) dataDirectory.mkdirs()
            return dataDirectory
        }

    /**
     * Gets the configuration file for a sign at the specified location.
     * 
     * @param location The location of the sign
     * @return The configuration file
     */
    @JvmStatic
    fun getSignFile(location: Location): File = _DATA_DIRECTORY_PATH.resolve("${locationToString(location)}.yml").toFile()

    /**
     * Loads the configuration for a sign at the specified location.
     * 
     * @param location The location of the sign
     * @return The configuration
     */
    fun loadSignConfig(location: Location): FileConfiguration = YamlConfiguration.loadConfiguration(getSignFile(location))
}