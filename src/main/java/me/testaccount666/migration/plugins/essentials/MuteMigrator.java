package me.testaccount666.migration.plugins.essentials;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.moderation.MuteModeration;
import me.testaccount666.serversystem.userdata.UserManager;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;

public class MuteMigrator extends AbstractMigrator {

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

            if (!essentialsUser.isMuted()) continue;

            var defaultReason = command("Moderation.DefaultReason", UserManager.Companion.getConsoleUser())
                    .target(essentialsUser.getName()).prefix(false).send(false).build();
            if (defaultReason.isEmpty()) {
                ServerSystem.getLog().severe("(MuteMigrator) Default reason is empty! This should not happen! (Mute Migration cancelled)");
                return count;
            }

            var muteManager = user.getMuteManager();
            var expireTime = essentialsUser.getMuteTimeout();
            var issueTime = System.currentTimeMillis(); // Issue time is lost

            var reason = essentialsUser.getMuteReason();
            if (reason == null) reason = defaultReason.get();

            var senderUUID = UserManager.Companion.getConsoleUser().getUuid(); // Sender UUID is lost
            var targetUUID = user.getUuid();

            muteManager.addModeration(new MuteModeration(false, issueTime, expireTime, reason, senderUUID, targetUUID));
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
                ServerSystem.getLog().warning("Couldn't find user '${uuid}', skipping mute migration!");
                continue;
            }

            var user = userOptional.get().getOfflineUser();

            ensureUserDataExists(uuid);
            var essentialsUser = essentials.getUser(uuid);

            var muteManager = user.getMuteManager();
            var muteOptional = muteManager.getActiveModeration();
            if (muteOptional.isEmpty()) continue;

            var mute = muteOptional.get();
            essentialsUser.setMuted(true);
            essentialsUser.setMuteReason(mute.reason());
            essentialsUser.setMuteTimeout(mute.expireTime());
            // Again, `issueTime` and `senderUUID` is lost

            count += 1;
        }

        return count;
    }
}
