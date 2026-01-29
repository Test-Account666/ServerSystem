package me.testaccount666.serversystem.commands.executables.waypoints.warp.manager

import me.testaccount666.serversystem.commands.executables.waypoints.WaypointManager
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import java.io.File

class WarpManager(_config: FileConfiguration, _file: File) : WaypointManager<Warp>(_config, _file) {
    override val root
        get() = "Warps"

    override fun build(name: String, location: Location) = Warp(name, location)
}
