package me.testaccount666.serversystem.userdata.home;

import org.bukkit.Location;

public class Home {
    private final String _name;
    private final Location _location;
    private String _displayName;

    public Home(String name, Location location) {
        _name = name.toLowerCase();
        _location = location;
    }

    public String getName() {
        return _name;
    }

    public String getDisplayName() {
        if (_displayName != null) return _displayName;

        _displayName = getName().toLowerCase();
        var chars = _displayName.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);

        _displayName = new String(chars);

        return _displayName;
    }

    public Location getLocation() {
        return _location;
    }
}
