package me.testaccount666.serversystem.userdata.persistence

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration

/**
 * A field handler for Bukkit Location objects.
 * This handler saves and loads locations with their world, coordinates, and rotation.
 */
class LocationFieldHandler : FieldHandler<Location> {
    override fun save(config: FileConfiguration, path: String, value: Location?) {
        if (value == null) {
            config.set(path, null)
            return
        }

        if (!value.isWorldLoaded) return

        config.set("${path}.World", value.world.name)
        config.set("${path}.X", value.x)
        config.set("${path}.Y", value.y)
        config.set("${path}.Z", value.z)
        config.set("${path}.Yaw", value.yaw)
        config.set("${path}.Pitch", value.pitch)
    }

    override fun load(config: FileConfiguration, path: String, defaultValue: Location?): Location? {
        if (!config.isSet(path)) return defaultValue

        val worldName: String = config.getString("${path}.World", "")!!

        val world = Bukkit.getWorld(worldName) ?: return defaultValue

        val x = config.getDouble("${path}.X")
        val y = config.getDouble("${path}.Y")
        val z = config.getDouble("${path}.Z")
        val yaw = config.getDouble("${path}.Yaw").toFloat()
        val pitch = config.getDouble("${path}.Pitch").toFloat()

        return Location(world, x, y, z, yaw, pitch)
    }
}