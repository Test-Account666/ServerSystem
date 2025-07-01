package me.testaccount666.serversystem.userdata.home;

import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.managers.globaldata.DefaultsData;
import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Manages homes for a specific user.
 * This class handles the creation, deletion, and retrieval of homes,
 * as well as loading and saving homes to the user's configuration file.
 */
public class HomeManager {
    private final OfflineUser _owner;
    private final File _userFile;
    private final Set<Home> _homes = new HashSet<>();
    private final FileConfiguration _config;

    /**
     * Creates a new HomeManager for the specified user.
     *
     * @param owner    The user who owns the homes
     * @param userFile The user's configuration file
     * @param config   The user's configuration
     */
    public HomeManager(OfflineUser owner, File userFile, FileConfiguration config) {
        _owner = owner;
        _userFile = userFile;
        _config = config;

        loadHomes();
    }

    /**
     * Gets all homes owned by the user.
     *
     * @return An unmodifiable set of all homes owned by the user
     */
    public Set<Home> getHomes() {
        return Collections.unmodifiableSet(_homes);
    }

    /**
     * Adds a new home with the specified name and location.
     * The home will be saved to the user's configuration file.
     *
     * @param name     The name of the home
     * @param location The location of the home
     */
    public void addHome(String name, Location location) {
        addHome(name, location, true);
    }

    /**
     * Adds a new home with the specified name and location.
     *
     * @param name      The name of the home
     * @param location  The location of the home
     * @param saveHomes Whether to save the homes to the user's configuration file
     */
    public void addHome(String name, Location location, boolean saveHomes) {
        addHome(new Home(name, location), saveHomes);
    }

    /**
     * Adds the specified home.
     * The home will be saved to the user's configuration file.
     *
     * @param home The home to add
     */
    public void addHome(Home home) {
        addHome(home, true);
    }

    /**
     * Adds the specified home.
     *
     * @param home      The home to add
     * @param saveHomes Whether to save the homes to the user's configuration file
     */
    public void addHome(Home home, boolean saveHomes) {
        _homes.add(home);

        if (saveHomes) saveHomes();
    }

    /**
     * Removes the home with the specified name.
     * The change will be saved to the user's configuration file.
     *
     * @param name The name of the home to remove
     */
    public void removeHome(String name) {
        _homes.removeIf(home -> home.getName().equalsIgnoreCase(name));

        saveHomes();
    }

    /**
     * Gets the home with the specified name.
     *
     * @param name The name of the home to get
     * @return An Optional containing the home, or an empty Optional if no home with the specified name exists
     */
    public Optional<Home> getHomeByName(String name) {
        return _homes.stream()
                .filter(home -> home.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Checks if a home with the specified name exists.
     *
     * @param name The name of the home to check for
     * @return true if a home with the specified name exists, false otherwise
     */
    public boolean hasHome(String name) {
        return getHomeByName(name).isPresent();
    }

    /**
     * Gets the maximum number of homes the user can have.
     * This is determined by the user's permissions.
     *
     * @return An Optional containing the maximum number of homes, or an empty Optional if the user is offline
     */
    public Optional<Integer> getMaxHomeCount() {
        if (!(_owner instanceof User user)) return Optional.empty();

        var defaultValue = DefaultsData.Home().getDefaultMaxHomes();
        var maxHomes = -1;

        var permissionPattern = PermissionManager.getPermission("Homes.MaxHomes");
        if (!permissionPattern.endsWith(".")) permissionPattern += ".";

        for (var effectivePermission : user.getPlayer().getEffectivePermissions()) {
            var permission = effectivePermission.getPermission();
            if (!permission.toLowerCase().startsWith(permissionPattern.toLowerCase())) continue;

            try {
                var parsed = Integer.parseInt(permission.substring(permissionPattern.length()));
                if (parsed > maxHomes) maxHomes = parsed;
            } catch (NumberFormatException ignored) {
                // I don't think we need to print this
            }
        }

        if (maxHomes == -1) maxHomes = defaultValue;

        return Optional.of(maxHomes);
    }


    private void saveHomes() {
        _config.set("User.Homes", null);

        for (var home : _homes) {
            var prefix = "User.Homes.${home.getName()}";

            _config.set("${prefix}.X", home.getLocation().getX());
            _config.set("${prefix}.Y", home.getLocation().getY());
            _config.set("${prefix}.Z", home.getLocation().getZ());

            _config.set("${prefix}.Yaw", home.getLocation().getYaw());
            _config.set("${prefix}.Pitch", home.getLocation().getPitch());

            _config.set("${prefix}.World", home.getLocation().getWorld().getName());
        }

        try {
            _config.save(_userFile);
        } catch (IOException exception) {
            throw new RuntimeException("Error saving homes for user '${_owner.getName()}' ('${_owner.getUuid()}')", exception);
        }
    }

    private void loadHomes() {
        _homes.clear();

        if (!_config.isConfigurationSection("User.Homes")) return;

        var homeNames = _config.getConfigurationSection("User.Homes").getKeys(false);

        for (var name : homeNames) {
            var prefix = "User.Homes.${name}";

            var home = parseHome(name, prefix);

            if (home.isEmpty()) continue;

            _homes.add(home.get());
        }
    }

    private Optional<Home> parseHome(String name, String prefix) {
        var x = _config.getDouble("${prefix}.X");
        var y = _config.getDouble("${prefix}.Y");
        var z = _config.getDouble("${prefix}.Z");

        var yaw = (float) _config.getDouble("${prefix}.Yaw");
        var pitch = (float) _config.getDouble("${prefix}.Pitch");

        var worldName = _config.getString("${prefix}.World", "");
        var world = Bukkit.getWorld(worldName);

        if (world == null) return Optional.empty();

        var location = new Location(world, x, y, z, yaw, pitch);

        return Optional.of(new Home(name, location));
    }
}
