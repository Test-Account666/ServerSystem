package me.testaccount666.serversystem.commands.executables.waypoints

import org.bukkit.Location

abstract class Waypoint(name: String, val location: Location) {
    val name = name.lowercase()

    val displayName by lazy { name.replaceFirstChar { it.uppercase() } }

    init {
        require(NAME_PATTERN.matches(name)) { "${this::class.simpleName} name must be alphanumeric!" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Waypoint) return false

        return name == other.name
    }

    override fun hashCode() = name.hashCode()

    companion object {
        val NAME_PATTERN = "[A-Za-z0-9_ÄÖÜüäöẞß]+".toRegex()
    }
}