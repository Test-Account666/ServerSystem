package me.testaccount666.serversystem.userdata.home;

import org.bukkit.Location;

import java.util.regex.Pattern;

/**
 * Represents a home location for a user.
 */
public class Home {
    private final static Pattern _HOME_NAME_PATTERN = Pattern.compile("[A-Za-z0-9_]+");
    private final String _name;
    private final Location _location;
    private String _displayName;

    /**
     * Creates a new home with the specified name and location.
     *
     * @param name     The name of the home (must be alphanumeric)
     * @param location The location of the home
     * @throws IllegalArgumentException if the home name is not alphanumeric
     */
    public Home(String name, Location location) {
        if (!_HOME_NAME_PATTERN.matcher(name).matches()) throw new IllegalArgumentException("Home name must be alphanumeric!");

        _name = name.toLowerCase();
        _location = location;
    }

    /**
     * Gets the name of this home.
     * The name is always stored in lowercase.
     *
     * @return The name of this home
     */
    public String getName() {
        return _name;
    }

    /**
     * Gets the display name of this home.
     * The display name is the name with the first letter capitalized.
     *
     * @return The display name of this home
     */
    public String getDisplayName() {
        if (_displayName != null) return _displayName;

        _displayName = getName().toLowerCase();
        var chars = _displayName.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);

        _displayName = new String(chars);

        return _displayName;
    }

    /**
     * Gets the location of this home.
     *
     * @return The location of this home
     */
    public Location getLocation() {
        return _location;
    }
}
