package me.testaccount666.serversystem.commands.executables.warp.manager

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import java.io.File
import java.io.IOException

class WarpManager(private val _config: FileConfiguration, private val _file: File) {
    private val _warps = HashSet<Warp>()

    init {
        loadWarps()
    }

    val warps
        get() = _warps.toSet()

    /**
     * Adds a new warp with the specified name and location.
     * The warp will be saved to the configuration file.
     * 
     * @param name     The name of the warp
     * @param location The location of the warp
     */
    @JvmOverloads
    fun addWarp(name: String, location: Location, saveWarps: Boolean = true): Warp = addWarp(Warp(name, location), saveWarps)

    /**
     * Adds the specified warp.
     * 
     * @param warp      The warp to add
     * @param saveWarps Whether to save the warp to the configuration file
     */
    @JvmOverloads
    fun addWarp(warp: Warp, saveWarps: Boolean = true): Warp {
        _warps.add(warp)

        if (saveWarps) saveWarps()
        return warp
    }

    /**
     * Removes the warp with the specified name.
     * The change will be saved to the configuration file.
     * 
     * @param name The name of the warp to remove
     */
    fun removeWarp(name: String) {
        _warps.removeIf { warp -> warp.name.equals(name, true) }

        saveWarps()
    }

    fun removeWarp(warp: Warp) {
        _warps.removeIf { savedWarp -> savedWarp.name.equals(warp.name, true) }

        saveWarps()
    }

    /**
     * Gets the warp with the specified name.
     * 
     * @param name The name of the warp to get
     * @return The warp, or null if no warp with the specified name exists
     */
    fun getWarpByName(name: String?) = _warps.firstOrNull { warp -> warp.name.equals(name, true) }

    /**
     * Checks if a warp with the specified name exists.
     * 
     * @param name The name of the warp to check for
     * @return true if a warp with the specified name exists, false otherwise
     */
    fun warpExists(name: String) = getWarpByName(name) != null


    private fun saveWarps() {
        _config.set("Warps", null)

        for (warp in _warps) {
            val prefix = "Warps.${warp.name}"

            _config.set("${prefix}.X", warp.location.x)
            _config.set("${prefix}.Y", warp.location.y)
            _config.set("${prefix}.Z", warp.location.z)

            _config.set("${prefix}.Yaw", warp.location.yaw)
            _config.set("${prefix}.Pitch", warp.location.pitch)

            _config.set("${prefix}.World", warp.location.world.name)
        }

        try {
            _config.save(_file)
        } catch (exception: IOException) {
            throw RuntimeException("Error saving warps file", exception)
        }
    }

    private fun loadWarps() {
        _warps.clear()

        if (!_config.isConfigurationSection("Warps")) return

        val warpNames = _config.getConfigurationSection("Warps")!!.getKeys(false)

        for (name in warpNames) {
            val prefix = "Warps.${name}"

            val warp = parseWarp(name, prefix) ?: continue

            _warps.add(warp)
        }
    }

    private fun parseWarp(name: String, prefix: String): Warp? {
        val x = _config.getDouble("${prefix}.X")
        val y = _config.getDouble("${prefix}.Y")
        val z = _config.getDouble("${prefix}.Z")

        val yaw = _config.getDouble("${prefix}.Yaw").toFloat()
        val pitch = _config.getDouble("${prefix}.Pitch").toFloat()

        val worldName: String = _config.getString("${prefix}.World", "")!!
        val world = Bukkit.getWorld(worldName) ?: return null

        val location = Location(world, x, y, z, yaw, pitch)

        return Warp(name, location)
    }
}
