package me.testaccount666.migration.plugins.essentials

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.moderation.MuteModeration
import me.testaccount666.serversystem.userdata.UserManager.Companion.consoleUser
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command

class MuteMigrator : AbstractMigrator() {
    override fun migrateFrom(): Int {
        var count = 0

        val userManager = userManager
        val essentials = essentials
        val uuids = essentials.users.allUserUUIDs

        for (uuid in uuids) {
            val cachedUser = userManager.getUserOrNull(uuid)
            if (cachedUser == null) {
                log.warning("Couldn't find user '${uuid}', skipping state migration!")
                continue
            }

            val essentialsUser = essentials.getUser(uuid)
            val user = cachedUser.offlineUser

            if (!essentialsUser.isMuted) continue

            val defaultReason = command("Moderation.DefaultReason", consoleUser) {
                target(essentialsUser.name)
                prefix(false)
                send(false)
                blankError(true)
            }.build()
            if (defaultReason.isEmpty()) {
                log.severe("(MuteMigrator) Default reason is empty! This should not happen! (Mute Migration cancelled)")
                return count
            }

            val muteManager = user.muteManager
            val expireTime = essentialsUser.muteTimeout
            val issueTime = System.currentTimeMillis() // Issue time is lost

            var reason = essentialsUser.muteReason
            if (reason == null) reason = defaultReason

            val senderUUID = consoleUser.uuid // Sender UUID is lost
            val targetUUID = user.uuid

            muteManager.addModeration(
                MuteModeration.builder().isShadowMute(false)
                    .targetUuid(targetUUID).issueTime(issueTime).expireTime(expireTime)
                    .senderUuid(senderUUID).reason(reason).build()
            )
            user.save()

            count += 1
        }

        return count
    }

    override fun migrateTo(): Int {
        var count = 0

        val userManager = userManager
        val essentials = essentials

        for (player in offlinePlayers()) {
            val uuid = player.uniqueId

            val cachedUser = userManager.getUserOrNull(uuid)
            if (cachedUser == null) {
                log.warning("Couldn't find user '${uuid}', skipping mute migration!")
                continue
            }

            val user = cachedUser.offlineUser

            ensureUserDataExists(uuid)
            val essentialsUser = essentials.getUser(uuid)

            val muteManager = user.muteManager
            val mute = muteManager.activeModeration ?: continue

            essentialsUser.muted = true
            essentialsUser.muteReason = mute.reason
            essentialsUser.muteTimeout = mute.expireTime

            // Again, `issueTime` and `senderUUID` is lost
            count += 1
        }

        return count
    }
}
