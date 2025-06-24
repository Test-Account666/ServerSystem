package me.testaccount666.serversystem.userdata;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    private static final Path USER_DATA_PATH = Path.of("plugins", "ServerSystem", "UserData");
    private static final ConsoleUser consoleUser = new ConsoleUser();
    //TODO: Make sure online Users get converted into OfflineUsers and vice versa upon joining/quitting
    private final Map<String, OfflineUser> userMap = new ConcurrentHashMap<>();
    private final Map<UUID, OfflineUser> userUuidMap = new ConcurrentHashMap<>();

    private static File getUserFile(UUID uuid) {
        return USER_DATA_PATH.resolve("${uuid}.yml").toFile();
    }

    private static OfflineUser createOfflineUser(UUID uuid) {
        return new OfflineUser(getUserFile(uuid));
    }

    private static User createUser(UUID uuid) {
        return new User(getUserFile(uuid));
    }

    public static ConsoleUser getConsoleUser() {
        return consoleUser;
    }

    private Optional<User> createAndStoreUser(String name) {
        var player = Bukkit.getPlayer(name);
        if (player == null) return Optional.empty();
        return createAndStoreUser(player.getUniqueId());
    }

    private Optional<User> createAndStoreUser(UUID uuid) {
        var player = Bukkit.getPlayer(uuid);
        if (player == null) return Optional.empty();
        var user = createUser(uuid);
        userMap.put(player.getName(), user);
        userUuidMap.put(uuid, user);
        return Optional.of(user);
    }

    private Optional<OfflineUser> createAndStoreOfflineUser(UUID uuid, String name) {
        var offlineUser = createOfflineUser(uuid);
        userMap.put(name, offlineUser);
        userUuidMap.put(uuid, offlineUser);
        return Optional.of(offlineUser);
    }

    public Optional<User> getUser(UUID uuid) {
        return getOfflineUser(uuid)
                .filter(User.class::isInstance)
                .map(User.class::cast)
                .or(() -> createAndStoreUser(uuid));
    }

    public Optional<User> getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public Optional<OfflineUser> getOfflineUser(String name) {
        if (userMap.containsKey(name)) return Optional.of(userMap.get(name));

        var offlinePlayer = Bukkit.getOfflinePlayer(name);
        if (offlinePlayer.getName() == null) return Optional.empty();

        return createAndStoreOfflineUser(offlinePlayer.getUniqueId(), offlinePlayer.getName());
    }

    public Optional<OfflineUser> getOfflineUser(UUID uuid) {
        if (userUuidMap.containsKey(uuid)) return Optional.of(userUuidMap.get(uuid));

        var offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.getName() == null) return Optional.empty();

        return createAndStoreOfflineUser(uuid, offlinePlayer.getName());
    }

    public Optional<User> getUser(String name) {
        return getOfflineUser(name)
                .filter(User.class::isInstance)
                .map(User.class::cast)
                .or(() -> createAndStoreUser(name));
    }
}