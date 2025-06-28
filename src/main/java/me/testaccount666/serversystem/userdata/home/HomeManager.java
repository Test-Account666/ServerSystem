package me.testaccount666.serversystem.userdata.home;

import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class HomeManager {
    private final OfflineUser owner;
    private final File userFile;
    private final Set<Home> homes = new HashSet<>();
    private final FileConfiguration config;

    public HomeManager(OfflineUser owner, File userFile, FileConfiguration config) {
        this.owner = owner;
        this.userFile = userFile;
        this.config = config;

        loadHomes();
    }

    public Set<Home> getHomes() {
        return homes;
    }

    public void addHome(String name, Location location) {
        addHome(name, location, true);
    }

    public void addHome(String name, Location location, boolean saveHomes) {
        addHome(new Home(name, location), saveHomes);
    }

    public void addHome(Home home) {
        addHome(home, true);
    }

    public void addHome(Home home, boolean saveHomes) {
        homes.add(home);

        if (saveHomes) saveHomes();
    }

    public void removeHome(String name) {
        homes.removeIf(home -> home.getName().equalsIgnoreCase(name));

        saveHomes();
    }

    public Optional<Home> getHomeByName(String name) {
        return homes.stream()
                .filter(home -> home.getName().equalsIgnoreCase(name))
                .findFirst();
    }


    public Optional<Integer> getMaxHomeCount() {
        if (!(owner instanceof User)) return Optional.empty();

        //TODO: Actually implement this!
        return null;
    }

    private void saveHomes() {
        config.set("User.Homes", null);

        for (var home : homes) {
            var prefix = "User.Homes.${home.getName()}";

            config.set("${prefix}.X", home.getLocation().getX());
            config.set("${prefix}.Y", home.getLocation().getY());
            config.set("${prefix}.Z", home.getLocation().getZ());

            config.set("${prefix}.Yaw", home.getLocation().getYaw());
            config.set("${prefix}.Pitch", home.getLocation().getPitch());

            config.set("${prefix}.World", home.getLocation().getWorld().getName());
        }

        try {
            config.save(userFile);
        } catch (IOException exception) {
            throw new RuntimeException("Error saving homes for user '${owner.getName()}' ('${owner.getUuid()}')", exception);
        }
    }

    private void loadHomes() {
        homes.clear();

        if (!config.isConfigurationSection("User.Homes")) return;

        var homeNames = config.getConfigurationSection("User.Homes").getKeys(false);

        for (var name : homeNames) {
            var prefix = "User.Homes.${name}";

            var x = config.getDouble("${prefix}.X");
            var y = config.getDouble("${prefix}.Y");
            var z = config.getDouble("${prefix}.Z");

            var yaw = (float) config.getDouble("${prefix}.Yaw");
            var pitch = (float) config.getDouble("${prefix}.Pitch");

            var worldName = config.getString("${prefix}.World", "");
            var world = Bukkit.getWorld(worldName);

            if (world == null) continue;

            var location = new Location(world, x, y, z, yaw, pitch);

            homes.add(new Home(name, location));
        }
    }
}
