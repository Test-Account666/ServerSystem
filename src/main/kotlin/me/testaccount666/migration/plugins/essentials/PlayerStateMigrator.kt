package me.testaccount666.migration.plugins.essentials

import me.testaccount666.serversystem.ServerSystem.Companion.log
import java.util.logging.Level

class PlayerStateMigrator : AbstractMigrator() {
    override fun migrateFrom(): Int {
        return essentials.users.allUserUUIDs.count { uuid ->
            runCatching {
                val cachedUser = userManager.getUserOrNull(uuid) ?: run {
                    log.warning("Couldn't find user '${uuid}', skipping state migration!")
                    return@count false
                }

                val essentialsUser = essentials.getUser(uuid)
                val user = cachedUser.offlineUser

                user.isAcceptsMessages = !essentialsUser.isIgnoreMsg
                user.isSocialSpyEnabled = essentialsUser.isSocialSpyEnabled

                user.isAcceptsTeleports = essentialsUser.isTeleportEnabled

                user.isGodMode = essentialsUser.isGodModeEnabled
                user.isVanish = essentialsUser.isVanished

                user.logoutPosition = essentialsUser.logoutLocation

                user.save()
                return@runCatching true
            }.onFailure { log.log(Level.WARNING, "Couldn't migrate state for '${uuid}'", it) }.getOrDefault(false)
        }
    }

    override fun migrateTo(): Int {
        return offlinePlayers().count { player ->
            val uuid = player.uniqueId
            runCatching {
                val cachedUser = userManager.getUserOrNull(uuid) ?: run {
                    log.warning("Couldn't find user '${uuid}', skipping state migration!")
                    return@count false
                }

                val user = cachedUser.offlineUser

                ensureUserDataExists(uuid)
                val essentialsUser = essentials.getUser(uuid)

                essentialsUser.isIgnoreMsg = !user.isAcceptsMessages
                essentialsUser.isSocialSpyEnabled = user.isSocialSpyEnabled

                essentialsUser.isTeleportEnabled = user.isAcceptsTeleports

                essentialsUser.isGodModeEnabled = user.isGodMode
                // Vanish state cannot be easily migrated to, if the user is offline
                if (cachedUser.isOnlineUser) essentialsUser.isVanished = user.isVanish

                essentialsUser.logoutLocation = user.logoutPosition

                return@runCatching true
            }.onFailure { log.log(Level.WARNING, "Couldn't migrate state for '${uuid}'", it) }.getOrDefault(false)
        }
    }
}
