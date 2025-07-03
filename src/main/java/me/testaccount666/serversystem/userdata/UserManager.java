package me.testaccount666.serversystem.userdata;

import me.testaccount666.serversystem.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages ServerSystem's user data.
 * This class is responsible for creating, caching, and retrieving user objects,
 * as well as cleaning up stale user data to prevent memory leaks.
 */
public class UserManager {
    public static final Path USER_DATA_PATH = Path.of("plugins", "ServerSystem", "UserData");
    private static final ConsoleUser _CONSOLE_USER = new ConsoleUser();
    private final Map<String, CachedUser> _userMap = new ConcurrentHashMap<>();
    private final Map<UUID, CachedUser> _userUuidMap = new ConcurrentHashMap<>();

    public UserManager() {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(ServerSystem.Instance, this::cleanStaleUsers, 15 * 20 * 60, 15 * 20 * 60); // 15 Minutes

        Bukkit.getOnlinePlayers().forEach(this::getUser);
    }

    private static File getUserFile(UUID uuid) {
        return USER_DATA_PATH.resolve("${uuid}.yml").toFile();
    }

    /**
     * Gets the console user instance.
     *
     * @return The console user instance
     */
    public static ConsoleUser getConsoleUser() {
        return _CONSOLE_USER;
    }

    /**
     * Gets a cached user by player instance.
     *
     * @param player The player to get the user for
     * @return An Optional containing the cached user or empty if the user doesn't exist
     */
    public Optional<CachedUser> getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    /**
     * Gets a cached user by UUID.
     * If the user is already cached, returns the cached instance.
     * If the user is online but not cached, creates and caches a new online user.
     * If the user is offline, creates and caches a new offline user.
     *
     * @param uuid The UUID of the user to get
     * @return An Optional containing the cached user or empty if the user doesn't exist
     */
    public Optional<CachedUser> getUser(UUID uuid) {
        if (_userUuidMap.containsKey(uuid)) {
            var cachedUser = _userUuidMap.get(uuid);
            cachedUser.updateLastAccessTime();

            return Optional.of(cachedUser);
        }

        var player = Bukkit.getPlayer(uuid);
        if (player != null) return Optional.of(createOnlineUser(uuid));

        return createOfflineUser(uuid);
    }

    /**
     * Gets a cached user by name.
     * If the user is already cached, returns the cached instance.
     * If the user is online but not cached, creates and caches a new online user.
     * If the user is offline, creates and caches a new offline user.
     *
     * @param name The name of the user to get
     * @return An Optional containing the cached user or empty if the user doesn't exist
     */
    public Optional<CachedUser> getUser(String name) {
        if (_userMap.containsKey(name)) {
            var cachedUser = _userMap.get(name);
            cachedUser.updateLastAccessTime();

            return Optional.of(cachedUser);
        }

        var player = Bukkit.getPlayer(name);
        if (player != null) return Optional.of(createOnlineUser(player.getUniqueId()));

        var offlineUser = Bukkit.getOfflinePlayer(name);
        if (offlineUser.getName() == null) return Optional.empty();

        return createOfflineUser(offlineUser.getUniqueId());
    }

    /**
     * Creates and caches an offline user for the given UUID.
     *
     * @param uuid The UUID of the user to create
     * @return An Optional containing the cached user or empty if the user doesn't exist
     */
    private Optional<CachedUser> createOfflineUser(UUID uuid) {
        var userFile = getUserFile(uuid);
        var user = new OfflineUser(userFile);

        if (user.getName().isEmpty()) return Optional.empty();

        var cachedUser = new CachedUser(user);
        _userUuidMap.put(uuid, cachedUser);
        _userMap.put(user.getName().get(), cachedUser);

        return Optional.of(cachedUser);
    }

    /**
     * Creates and caches an online user for the given UUID.
     *
     * @param uuid The UUID of the user to create
     * @return The cached user
     */
    private CachedUser createOnlineUser(UUID uuid) {
        var userFile = getUserFile(uuid);
        var user = new User(userFile);

        var cachedUser = new CachedUser(user);
        _userUuidMap.put(uuid, cachedUser);
        _userMap.put(user.getName().get(), cachedUser);

        return cachedUser;
    }

    /**
     * Gets a copy of all currently cached users.
     *
     * @return A set containing all cached users
     */
    public Set<CachedUser> getCachedUsers() {
        return Set.copyOf(_userMap.values());
    }

    /**
     * Removes stale users from the cache to prevent memory leaks.
     * This method is called periodically by a scheduled task.
     */
    public void cleanStaleUsers() {
        var staleUsers = _userMap.values().stream().filter(CachedUser::isStale).collect(Collectors.toSet());

        _userMap.entrySet().removeIf(entry -> staleUsers.contains(entry.getValue()));
        _userUuidMap.entrySet().removeIf(entry -> staleUsers.contains(entry.getValue()));
    }
}
