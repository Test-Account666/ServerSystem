package me.testaccount666.serversystem.userdata.home

import me.testaccount666.serversystem.commands.executables.waypoints.Waypoint
import org.bukkit.Location

/**
 * Represents a home location for a user.
 */
class Home internal constructor(name: String, location: Location) : Waypoint(name.lowercase(), location) {
    companion object {
        @JvmStatic
        fun of(name: String, location: Location): Home? {
            if (!NAME_PATTERN.matches(name)) return null

            return Home(name, location)
        }
    }
}