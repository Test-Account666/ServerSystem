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

public class UserManager {
    public static final Path USER_DATA_PATH = Path.of("plugins", "ServerSystem", "UserData");
    private static final ConsoleUser _CONSOLE_USER = new ConsoleUser();
    private final Map<String, CachedUser> _userMap = new ConcurrentHashMap<>();
    private final Map<UUID, CachedUser> _userUuidMap = new ConcurrentHashMap<>();

    public UserManager() {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(ServerSystem.Instance, this::cleanStaleUsers, 15 * 20 * 60, 15 * 20 * 60); // 15 Minutes
    }

    private static File getUserFile(UUID uuid) {
        return USER_DATA_PATH.resolve("${uuid}.yml").toFile();
    }

    public static ConsoleUser getConsoleUser() {
        return _CONSOLE_USER;
    }

    public Optional<CachedUser> getUser(Player player) {
        return getUser(player.getUniqueId());
    }

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

    private Optional<CachedUser> createOfflineUser(UUID uuid) {
        var userFile = getUserFile(uuid);
        var user = new OfflineUser(userFile);

        if (user.getName().isEmpty()) return Optional.empty();

        var cachedUser = new CachedUser(user);
        _userUuidMap.put(uuid, cachedUser);
        _userMap.put(user.getName().get(), cachedUser);

        return Optional.of(cachedUser);
    }

    private CachedUser createOnlineUser(UUID uuid) {
        var userFile = getUserFile(uuid);
        var user = new User(userFile);

        var cachedUser = new CachedUser(user);
        _userUuidMap.put(uuid, cachedUser);
        _userMap.put(user.getName().get(), cachedUser);

        return cachedUser;
    }

    public Set<CachedUser> getCachedUsers() {
        return Set.copyOf(_userMap.values());
    }

    public void cleanStaleUsers() {
        var staleUsers = _userMap.values().stream().filter(CachedUser::isStale).collect(Collectors.toSet());

        _userMap.entrySet().removeIf(entry -> staleUsers.contains(entry.getValue()));
        _userUuidMap.entrySet().removeIf(entry -> staleUsers.contains(entry.getValue()));
    }
}