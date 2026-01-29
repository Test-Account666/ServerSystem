package me.testaccount666.serversystem.userdata.home

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.commands.executables.waypoints.WaypointManager
import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.managers.globaldata.DefaultsData
import me.testaccount666.serversystem.userdata.OfflineUser
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration

/**
 * Manages homes for a specific user.
 * This class handles the creation, deletion, and retrieval of homes,
 * as well as loading and saving homes to the user's configuration file.
 */
class HomeManager(private val _owner: OfflineUser, _config: FileConfiguration) : WaypointManager<Home>(_config, _owner.userFile) {
    override val root
        get() = "User.Homes"

    override fun build(name: String, location: Location) = Home(name, location)

    override fun canAddPoints() = size < (maxHomeCount ?: (size + 1))

    val maxHomeCount: Int?
        /**
         * Gets the maximum number of homes the user can have.
         * This is determined by the user's permissions.
         *
         * @return The maximum number of homes, or null if the user is offline
         */
        get() {
            if (_owner !is User) return null

            if (PermissionManager.hasPermission(_owner, "Homes.Unlimited", false)) return Int.MAX_VALUE

            val defaultValue = DefaultsData.home().defaultMaxHomes
            var maxHomes = -1

            var permissionPattern = PermissionManager.getPermission("Homes.MaxHomes") ?: run {
                ServerSystem.log.warning("Homes.MaxHomes permission not found! Using default value of $defaultValue")
                return defaultValue
            }
            if (!permissionPattern.endsWith(".")) permissionPattern += "."

            for (effectivePermission in _owner.getPlayer()!!.effectivePermissions) {
                val permission = effectivePermission.permission
                if (!permission.startsWith(permissionPattern, true)) continue

                val parsed = permission.drop(permissionPattern.length).toIntOrNull() ?: run {
                    ServerSystem.log.warning("Invalid value for Homes.MaxHomes permission: $permission")
                    continue
                }
                if (parsed > maxHomes) maxHomes = parsed
            }

            return if (maxHomes == -1) defaultValue else maxHomes
        }
}