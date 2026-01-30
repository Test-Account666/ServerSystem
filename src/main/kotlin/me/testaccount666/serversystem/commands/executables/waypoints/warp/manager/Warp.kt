package me.testaccount666.serversystem.commands.executables.waypoints.warp.manager

import me.testaccount666.serversystem.commands.executables.waypoints.Waypoint
import org.bukkit.Location

class Warp internal constructor(name: String, location: Location) : Waypoint(name.lowercase(), location) {
    companion object {
        fun of(name: String, location: Location): Warp? {
            if (!NAME_PATTERN.matches(name)) return null

            return Warp(name, location)
        }
    }
}
