package me.testaccount666.serversystem.userdata.home

import org.bukkit.Location
import java.util.*
import java.util.regex.Pattern

/**
 * Represents a home location for a user.
 */
class Home internal constructor(name: String, location: Location) {
    val name: String
    val location: Location
    private var _displayName: String? = null

    /**
     * Creates a new home with the specified name and location.
     *
     * @param name     The name of the home (must be alphanumeric)
     * @param location The location of the home
     * @throws IllegalArgumentException if the home name is not alphanumeric
     */
    init {
        require(_HOME_NAME_PATTERN.matcher(name).matches()) { "Home name must be alphanumeric" }

        this.name = name.lowercase(Locale.getDefault())
        this.location = location
    }

    val displayName: String
        /**
         * Gets the display name of this home.
         * The display name is the name with the first letter capitalized.
         *
         * @return The display name of this home
         */
        get() {
            if (_displayName != null) return _displayName!!

            _displayName = name.lowercase(Locale.getDefault())
            val chars = _displayName!!.toCharArray()
            chars[0] = chars[0].uppercaseChar()

            _displayName = String(chars)

            return _displayName!!
        }

    companion object {
        private val _HOME_NAME_PATTERN: Pattern = Pattern.compile("[A-Za-z0-9_]+")

        @JvmStatic
        fun of(name: String, location: Location): Optional<Home> {
            if (!_HOME_NAME_PATTERN.matcher(name).matches()) return Optional.empty()

            return Optional.of(Home(name, location))
        }
    }
}