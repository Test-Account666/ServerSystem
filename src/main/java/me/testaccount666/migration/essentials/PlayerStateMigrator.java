package me.testaccount666.migration.essentials;

import me.testaccount666.serversystem.ServerSystem;

public class PlayerStateMigrator extends AbstractMigrator {

    @Override
    public int migrateFrom() {
        var count = 0;

        var userManager = userManager();
        var essentials = essentials();
        var uuids = essentials.getUsers().getAllUserUUIDs();

        for (var uuid : uuids) {
            var userOptional = userManager.getUser(uuid);
            if (userOptional.isEmpty()) {
                ServerSystem.getLog().warning("Couldn't find user '${uuid}', skipping state migration!");
                continue;
            }

            var essentialsUser = essentials.getUser(uuid);
            var user = userOptional.get().getOfflineUser();

            user.setAcceptsMessages(!essentialsUser.isIgnoreMsg());
            user.setSocialSpyEnabled(essentialsUser.isSocialSpyEnabled());

            user.setAcceptsTeleports(essentialsUser.isTeleportEnabled());

            user.setGodMode(essentialsUser.isGodModeEnabled());
            user.setVanish(essentialsUser.isVanished());

            user.setLogoutPosition(essentialsUser.getLogoutLocation());

            user.save();
            count += 1;
        }

        return count;
    }

    @Override
    public int migrateTo() {
        var count = 0;

        var userManager = userManager();
        var essentials = essentials();

        for (var player : offlinePlayers()) {
            var uuid = player.getUniqueId();

            var userOptional = userManager.getUser(uuid);
            if (userOptional.isEmpty()) {
                ServerSystem.getLog().warning("Couldn't find user '${uuid}', skipping state migration!");
                continue;
            }

            var user = userOptional.get().getOfflineUser();

            ensureUserDataExists(uuid);
            var essentialsUser = essentials.getUser(uuid);

            essentialsUser.setIgnoreMsg(!user.isAcceptsMessages());
            essentialsUser.setSocialSpyEnabled(user.isSocialSpyEnabled());

            essentialsUser.setTeleportEnabled(user.isAcceptsTeleports());

            essentialsUser.setGodModeEnabled(user.isGodMode());
            essentialsUser.setVanished(user.isVanish());

            essentialsUser.setLogoutLocation(user.getLogoutPosition());

            count += 1;
        }

        return count;
    }
}
