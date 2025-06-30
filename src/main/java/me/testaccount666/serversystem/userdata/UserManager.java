package me.testaccount666.serversystem.userdata;

import me.testaccount666.serversystem.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserManager {
    private static final Path USER_DATA_PATH = Path.of("plugins", "ServerSystem", "UserData");
    private static final ConsoleUser consoleUser = new ConsoleUser();
    private final Map<String, CachedUser> userMap = new ConcurrentHashMap<>();
    private final Map<UUID, CachedUser> userUuidMap = new ConcurrentHashMap<>();

    public UserManager() {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(ServerSystem.Instance, this::cleanStaleUsers, 15 * 20 * 60, 15 * 20 * 60); // 15 Minutes
    }

    private static File getUserFile(UUID uuid) {
        return USER_DATA_PATH.resolve("${uuid}.yml").toFile();
    }

    public static ConsoleUser getConsoleUser() {
        return consoleUser;
    }

    public Optional<CachedUser> getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public Optional<CachedUser> getUser(UUID uuid) {
        if (userUuidMap.containsKey(uuid)) {
            var cachedUser = userUuidMap.get(uuid);
            cachedUser.updateLastAccessTime();

            return Optional.ofNullable(cachedUser);
        }

        var player = Bukkit.getPlayer(uuid);
        if (player != null) return Optional.ofNullable(createOnlineUser(uuid));

        return createOfflineUser(uuid);
    }

    public Optional<CachedUser> getUser(String name) {
        if (userMap.containsKey(name)) {
            var cachedUser = userMap.get(name);
            cachedUser.updateLastAccessTime();

            return Optional.ofNullable(cachedUser);
        }

        var player = Bukkit.getPlayer(name);
        if (player != null) return Optional.ofNullable(createOnlineUser(player.getUniqueId()));

        var offlineUser = Bukkit.getOfflinePlayer(name);
        if (offlineUser.getName() == null) return Optional.empty();

        return createOfflineUser(offlineUser.getUniqueId());
    }

    private Optional<CachedUser> createOfflineUser(UUID uuid) {
        var userFile = getUserFile(uuid);
        var user = new OfflineUser(userFile);

        var cachedUser = new CachedUser(user);
        userUuidMap.put(uuid, cachedUser);
        userMap.put(user.getName(), cachedUser);

        return Optional.ofNullable(cachedUser);
    }

    private CachedUser createOnlineUser(UUID uuid) {
        var userFile = getUserFile(uuid);
        var user = new User(userFile);

        var cachedUser = new CachedUser(user);
        userUuidMap.put(uuid, cachedUser);
        userMap.put(user.getName(), cachedUser);

        return cachedUser;
    }

    public void cleanStaleUsers() {
        var staleUsers = userMap.values().stream().filter(CachedUser::isStale).collect(Collectors.toSet());

        userMap.entrySet().removeIf(entry -> staleUsers.contains(entry.getValue()));
        userUuidMap.entrySet().removeIf(entry -> staleUsers.contains(entry.getValue()));
    }
}