package me.testaccount666.serversystem.userdata.home;

import org.bukkit.Location;

public class Home {
    private final String name;
    private final Location location;
    private String displayName;

    public Home(String name, Location location) {
        this.name = name.toLowerCase();
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        if (displayName != null) return displayName;

        displayName = getName().toLowerCase();
        var chars = displayName.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);

        displayName = new String(chars);

        return displayName;
    }

    public Location getLocation() {
        return location;
    }
}
