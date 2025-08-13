package me.testaccount666.migration.essentials;

import com.earth2me.essentials.Essentials;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static me.testaccount666.migration.essentials.EssentialsMigrator.ensureUserDataExists;

public class HomeMigrator {

    public Map<UUID, Integer> migrateFrom() {
        var count = new HashMap<UUID, Integer>();

        var userManager = ServerSystem.Instance.getRegistry().getService(UserManager.class);
        var essentials = Essentials.getPlugin(Essentials.class);
        var uuids = essentials.getUsers().getAllUserUUIDs();

        for (var uuid : uuids) {
            var userOptional = userManager.getUser(uuid);
            if (userOptional.isEmpty()) {
                ServerSystem.getLog().warning("Couldn't find user '${uuid}', skipping home migration!");
                continue;
            }

            var user = userOptional.get().getOfflineUser();

            count.put(user.getUuid(), migrateFrom(user));
        }

        return count;
    }

    private int migrateFrom(OfflineUser user) {
        var count = 0;

        var homeManager = user.getHomeManager();
        var essentials = Essentials.getPlugin(Essentials.class);

        var essentialsUser = essentials.getUser(user.getUuid());
        for (var homeName : essentialsUser.getHomes())
            try {
                var location = essentialsUser.getHome(homeName);

                homeManager.addHome(homeName, location);
                count += 1;
            } catch (IllegalArgumentException exception) {
                var userName = user.getName().orElse("Unknown");

                ServerSystem.getLog().log(Level.WARNING, "Couldn't migrate home '${homeName}' for user '${user.getUuid()}' (${userName})", exception);
            }

        return count;
    }

    public Map<UUID, Integer> migrateTo() {
        var count = new HashMap<UUID, Integer>();

        var userManager = ServerSystem.Instance.getRegistry().getService(UserManager.class);

        for (var player : Bukkit.getOfflinePlayers()) {
            var uuid = player.getUniqueId();

            var userOptional = userManager.getUser(uuid);
            if (userOptional.isEmpty()) {
                ServerSystem.getLog().warning("Couldn't find user '${uuid}', skipping home migration!");
                continue;
            }

            count.put(uuid, migrateTo(userOptional.get().getOfflineUser()));
        }

        return count;
    }

    private int migrateTo(OfflineUser user) {
        var count = 0;

        var homeManager = user.getHomeManager();
        var essentials = Essentials.getPlugin(Essentials.class);

        ensureUserDataExists(user.getUuid());
        var essentialsUser = essentials.getUser(user.getUuid());

        for (var home : homeManager.getHomes()) {
            var homeName = home.getDisplayName();
            var location = home.getLocation();

            try {
                essentialsUser.setHome(homeName, location);

                count += 1;
            } catch (Exception exception) {
                var userName = user.getName().orElse("Unknown");

                ServerSystem.getLog().log(Level.WARNING, "Couldn't migrate home '${homeName}' for user '${user.getUuid()}' (${userName})", exception);
            }
        }

        return count;
    }
}
