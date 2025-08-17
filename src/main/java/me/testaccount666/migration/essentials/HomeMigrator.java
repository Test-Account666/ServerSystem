package me.testaccount666.migration.essentials;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.OfflineUser;

import java.util.logging.Level;


public class HomeMigrator extends AbstractMigrator {

    @Override
    public int migrateFrom() {
        var count = 0;

        var userManager = userManager();
        var essentials = essentials();
        var uuids = essentials.getUsers().getAllUserUUIDs();

        for (var uuid : uuids) {
            var userOptional = userManager.getUser(uuid);
            if (userOptional.isEmpty()) {
                ServerSystem.getLog().warning("Couldn't find user '${uuid}', skipping home migration!");
                continue;
            }

            count += migrateFrom(userOptional.get().getOfflineUser());
        }

        return count;
    }

    private int migrateFrom(OfflineUser user) {
        var count = 0;

        var homeManager = user.getHomeManager();
        var essentials = essentials();

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

        user.save();

        return count;
    }

    @Override
    public int migrateTo() {
        var count = 0;

        var userManager = userManager();

        for (var player : offlinePlayers()) {
            var uuid = player.getUniqueId();

            var userOptional = userManager.getUser(uuid);
            if (userOptional.isEmpty()) {
                ServerSystem.getLog().warning("Couldn't find user '${uuid}', skipping home migration!");
                continue;
            }

            count += migrateTo(userOptional.get().getOfflineUser());
        }

        return count;
    }

    private int migrateTo(OfflineUser user) {
        var count = 0;

        var homeManager = user.getHomeManager();
        var essentials = essentials();

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
