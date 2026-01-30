package me.testaccount666.serversystem.commands.executables.waypoints.home

import me.testaccount666.serversystem.commands.executables.waypoints.AbstractCommandWaypoint
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.home.Home
import me.testaccount666.serversystem.userdata.home.HomeManager
import org.bukkit.Location
import org.bukkit.command.Command

abstract class AbstractCommandHome : AbstractCommandWaypoint<HomeManager, Home>() {
    override fun createPoint(name: String, location: Location) = Home.of(name, location)
    override fun getPlaceholder() = "<HOME>"

    override fun getWaypointManager(command: Command, targetUser: User) = targetUser.homeManager
}
