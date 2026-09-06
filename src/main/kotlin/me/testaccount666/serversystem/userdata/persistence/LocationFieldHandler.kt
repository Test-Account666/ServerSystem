package me.testaccount666.serversystem.userdata.persistence

import me.testaccount666.paperktx.extensions.getValue
import me.testaccount666.paperktx.extensions.location
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
            config[path] = null
            return
        }

        if (!value.isWorldLoaded) return

        config["${path}.World"] = value.world.name
        config["${path}.X"] = value.x
        config["${path}.Y"] = value.y
        config["${path}.Z"] = value.z
        config["${path}.Yaw"] = value.yaw
        config["${path}.Pitch"] = value.pitch
    }

    override fun load(config: FileConfiguration, path: String, defaultValue: Location?): Location? {
        if (!config.isSet(path)) return defaultValue

        val world = config.getValue("${path}.World", "").let(Bukkit::getWorld) ?: return defaultValue

        val x = config.getDouble("${path}.X")
        val y = config.getDouble("${path}.Y")
        val z = config.getDouble("${path}.Z")
        val yaw = config.getDouble("${path}.Yaw")
        val pitch = config.getDouble("${path}.Pitch")

        return location(x, y, z, world, yaw, pitch)
    }
}