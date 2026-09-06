package me.testaccount666.migration.plugins.essentials

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.moderation.MuteModeration
import me.testaccount666.serversystem.userdata.UserManager.Companion.consoleUser
import java.util.logging.Level

class MuteMigrator : AbstractMigrator() {
    override fun migrateFrom(): Int {
        return essentials.users.allUserUUIDs.count { uuid ->
            runCatching {
                val cachedUser = userManager.getUserOrNull(uuid) ?: run {
                    log.warning("Couldn't find user '${uuid}', skipping state migration!")
                    return@count false
                }

                val essentialsUser = essentials.getUser(uuid)
                val user = cachedUser.offlineUser

                if (!essentialsUser.isMuted) return@count false

                val defaultReason = consoleUser.commandMsg("Moderation.DefaultReason") {
                    target(essentialsUser.name)
                    prefix(false)
                    send(false)
                    blankError(true)
                }

                if (defaultReason.isEmpty()) {
                    log.severe("(MuteMigrator) Default reason is empty! This should not happen!")
                    return@count false
                }

                val muteManager = user.muteManager
                val expireTime = essentialsUser.muteTimeout
                val issueTime = System.currentTimeMillis() // Issue time is lost

                val reason = essentialsUser.muteReason ?: defaultReason

                val senderUUID = consoleUser.uuid // Sender UUID is lost
                val targetUUID = user.uuid

                muteManager.addModeration(
                    MuteModeration.builder {
                        isShadowMute(false)
                        targetUuid(targetUUID)
                        issueTime(issueTime)
                        expireTime(expireTime)
                        senderUuid(senderUUID)
                        reason(reason)
                    }
                )
                user.save()

                return@runCatching true
            }.onFailure { log.log(Level.WARNING, "Couldn't migrate mute for '${uuid}'", it) }.getOrDefault(false)
        }
    }

    override fun migrateTo(): Int {
        return offlinePlayers().count { player ->
            val uuid = player.uniqueId

            runCatching {
                val cachedUser = userManager.getUserOrNull(uuid) ?: run {
                    log.warning("Couldn't find user '${uuid}', skipping mute migration!")
                    return@count false
                }

                val user = cachedUser.offlineUser

                ensureUserDataExists(uuid)
                val essentialsUser = essentials.getUser(uuid)

                val muteManager = user.muteManager
                val mute = muteManager.activeModeration ?: return@count false

                essentialsUser.muted = true
                essentialsUser.muteReason = mute.reason
                essentialsUser.muteTimeout = mute.expireTime

                // Again, `issueTime` and `senderUUID` is lost
                return@runCatching true
            }.onFailure { log.log(Level.WARNING, "Couldn't migrate mute for '${uuid}'", it) }.getOrDefault(false)
        }
    }
}
