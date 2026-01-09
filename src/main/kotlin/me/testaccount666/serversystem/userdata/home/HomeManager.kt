package me.testaccount666.serversystem.userdata.home

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.managers.globaldata.DefaultsData
import me.testaccount666.serversystem.userdata.OfflineUser
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import java.util.*

/**
 * Manages homes for a specific user.
 * This class handles the creation, deletion, and retrieval of homes,
 * as well as loading and saving homes to the user's configuration file.
 */
class HomeManager(private val _owner: OfflineUser, private val _config: FileConfiguration) {
    private val _homes: MutableSet<Home> = HashSet()

    /**
     * Creates a new HomeManager for the specified user.
     *
     * @param _owner  The user who owns the homes
     * @param _config The user's configuration
     */
    init {
        loadHomes()
    }

    val homes: MutableSet<Home>
        get() = Collections.unmodifiableSet<Home>(_homes)


    /**
     * Adds a new home with the specified name and location.
     *
     * @param name      The name of the home
     * @param location  The location of the home
     * @param saveHomes Whether to save the homes to the user's configuration file
     */
    @JvmOverloads
    fun addHome(name: String, location: Location, saveHomes: Boolean = true) = addHome(Home(name, location), saveHomes)

    /**
     * Adds the specified home.
     *
     * @param home      The home to add
     * @param saveHomes Whether to save the homes to the user's configuration file
     */
    @JvmOverloads
    fun addHome(home: Home, saveHomes: Boolean = true) {
        _homes.add(home)

        if (saveHomes) saveHomes()
    }

    /**
     * Removes the home with the specified name.
     * The change will be saved to the user's configuration file.
     *
     * @param name The name of the home to remove
     */
    fun removeHome(name: String) {
        _homes.removeIf { home -> home.name.equals(name, ignoreCase = true) }

        saveHomes()
    }

    fun removeHome(home: Home) {
        _homes.removeIf { savedHome -> savedHome.name.equals(home.name, ignoreCase = true) }

        saveHomes()
    }

    /**
     * Gets the home with the specified name.
     *
     * @param name The name of the home to get
     * @return An Optional containing the home, or an empty Optional if no home with the specified name exists
     */
    fun getHomeByName(name: String): Optional<Home> {
        return _homes.stream()
            .filter { home -> home.name.equals(name, ignoreCase = true) }
            .findFirst()
    }

    /**
     * Checks if a home with the specified name exists.
     *
     * @param name The name of the home to check for
     * @return true if a home with the specified name exists, false otherwise
     */
    fun hasHome(name: String): Boolean = getHomeByName(name).isPresent

    val maxHomeCount: Optional<Int>
        /**
         * Gets the maximum number of homes the user can have.
         * This is determined by the user's permissions.
         *
         * @return An Optional containing the maximum number of homes, or an empty Optional if the user is offline
         */
        get() {
            if (_owner !is User) return Optional.empty()

            if (PermissionManager.hasPermission(
                    _owner,
                    "Homes.Unlimited",
                    false
                )) return Optional.of(Int.MAX_VALUE)

            val defaultValue = DefaultsData.home().defaultMaxHomes
            var maxHomes = -1

            var permissionPattern = PermissionManager.getPermission("Homes.MaxHomes")
            if (permissionPattern == null) {
                ServerSystem.log.warning("Homes.MaxHomes permission not found! Using default value of $defaultValue")
                return Optional.of(defaultValue)
            }
            if (!permissionPattern.endsWith(".")) permissionPattern += "."

            for (effectivePermission in _owner.getPlayer()!!.effectivePermissions) {
                val permission = effectivePermission.permission
                if (!permission.lowercase(Locale.getDefault()).startsWith(permissionPattern.lowercase(Locale.getDefault()))) continue

                try {
                    val parsed = permission.substring(permissionPattern.length).toInt()
                    if (parsed > maxHomes) maxHomes = parsed
                } catch (ignored: NumberFormatException) {
                    // I don't think we need to print this
                }
            }

            if (maxHomes == -1) maxHomes = defaultValue

            return Optional.of(maxHomes)
        }


    private fun saveHomes() {
        _config.set("User.Homes", null)

        for (home in _homes) {
            val prefix = "User.Homes.${home.name}"

            _config.set("${prefix}.X", home.location.x)
            _config.set("${prefix}.Y", home.location.y)
            _config.set("${prefix}.Z", home.location.z)

            _config.set("${prefix}.Yaw", home.location.yaw)
            _config.set("${prefix}.Pitch", home.location.pitch)

            _config.set("${prefix}.World", home.location.world.name)
        }

        _owner.save()
    }

    private fun loadHomes() {
        _homes.clear()

        if (!_config.isConfigurationSection("User.Homes")) return

        val homeNames = _config.getConfigurationSection("User.Homes")!!.getKeys(false)

        for (name in homeNames) {
            val prefix = "User.Homes.${name}"

            val home = parseHome(name, prefix)

            if (home.isEmpty) continue

            _homes.add(home.get())
        }
    }

    private fun parseHome(name: String, prefix: String): Optional<Home> {
        val x = _config.getDouble("${prefix}.X")
        val y = _config.getDouble("${prefix}.Y")
        val z = _config.getDouble("${prefix}.Z")

        val yaw = _config.getDouble("${prefix}.Yaw").toFloat()
        val pitch = _config.getDouble("${prefix}.Pitch").toFloat()

        val worldName: String = _config.getString("${prefix}.World", "")!!
        val world = Bukkit.getWorld(worldName) ?: return Optional.empty()

        val location = Location(world, x, y, z, yaw, pitch)

        return Optional.of(Home(name, location))
    }
}