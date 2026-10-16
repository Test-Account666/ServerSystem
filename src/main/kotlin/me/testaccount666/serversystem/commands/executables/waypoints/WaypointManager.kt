package me.testaccount666.serversystem.commands.executables.waypoints

import me.testaccount666.paperktx.extensions.getValue
import me.testaccount666.paperktx.extensions.location
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import java.io.File
import java.io.IOException

abstract class WaypointManager<T : Waypoint>(private val _config: FileConfiguration, private val _file: File) {
    private val _waypoints = mutableMapOf<String, T>()

    init {
        loadPoints()
    }

    val waypoints
        get() = _waypoints.values.toSet()

    val size
        get() = _waypoints.size

    fun isEmpty() = _waypoints.isEmpty()

    /**
     * Adds a new waypoint with the specified name and location.
     * The waypoint will be saved to the configuration file.
     *
     * @param name     The name of the waypoint
     * @param location The location of the waypoint
     */
    @JvmOverloads
    fun addPoint(name: String, location: Location, save: Boolean = true) = addPoint(build(name, location), save)

    /**
     * Adds the specified waypoint.
     *
     * @param point      The waypoint to add
     * @param save Whether to save the waypoint to the configuration file
     */
    @JvmOverloads
    fun addPoint(point: T, save: Boolean = true): T {
        val previous = _waypoints.put(point.name, point)

        if (save && previous != point) savePoints()
        return point
    }

    /**
     * Removes the waypoint with the specified name.
     * The change will be saved to the configuration file.
     *
     * @param name The name of the waypoint to remove
     */
    fun removePoint(name: String) {
        if (_waypoints.remove(name.lowercase()) == null) return
        savePoints()
    }

    fun removePoint(point: T) {
        if (_waypoints.remove(point.name) == null) return
        savePoints()
    }

    /**
     * Gets the waypoint with the specified name.
     *
     * @param name The name of the waypoint to get
     * @return The waypoint, or null if no waypoint with the specified name exists
     */
    fun getPointByName(name: String?) = name?.let { _waypoints[it.lowercase()] }

    /**
     * Checks if a waypoint with the specified name exists.
     *
     * @param name The name of the waypoint to check for
     * @return true if a waypoint with the specified name exists, false otherwise
     */
    fun pointExists(name: String) = _waypoints.containsKey(name.lowercase())

    protected abstract val root: String

    private fun savePoints() {
        _config[root] = null

        _waypoints.values.forEach {
            val prefix = "${root}.${it.name}"

            _config["${prefix}.X"] = it.location.x
            _config["${prefix}.Y"] = it.location.y
            _config["${prefix}.Z"] = it.location.z

            _config["${prefix}.Yaw"] = it.location.yaw
            _config["${prefix}.Pitch"] = it.location.pitch

            _config["${prefix}.World"] = it.location.world.name
        }

        try {
            _config.save(_file)
        } catch (exception: IOException) {
            throw RuntimeException("Error saving ${this::class.simpleName} file", exception)
        }
    }

    private fun loadPoints() {
        _waypoints.clear()

        if (!_config.isConfigurationSection(root)) return

        val pointNames = _config.getConfigurationSection(root)!!.getKeys(false)

        for (name in pointNames) {
            val prefix = "${root}.${name}"

            parsePoint(name, prefix)?.let { _waypoints[it.name] = it }
        }
    }

    private fun parsePoint(name: String, prefix: String): T? {
        val x = _config.getDouble("${prefix}.X")
        val y = _config.getDouble("${prefix}.Y")
        val z = _config.getDouble("${prefix}.Z")

        val yaw = _config.getDouble("${prefix}.Yaw")
        val pitch = _config.getDouble("${prefix}.Pitch")

        val world = _config.getValue("${prefix}.World", "").let(Bukkit::getWorld) ?: return null
        return build(name, location(x, y, z, world, yaw, pitch))
    }

    open fun canAddPoints() = true

    protected abstract fun build(name: String, location: Location): T
}

