package me.testaccount666.serversystem.commands.executables.waypoints.home

import me.testaccount666.serversystem.commands.executables.waypoints.AbstractTabCompleterWaypoint
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.home.Home
import me.testaccount666.serversystem.userdata.home.HomeManager
import org.bukkit.command.Command

abstract class AbstractTabCompleterHome : AbstractTabCompleterWaypoint<HomeManager, Home>() {
    override fun getWaypointManager(command: Command, targetUser: User) = targetUser.homeManager
}