package me.testaccount666.migration.plugins.essentials

import me.testaccount666.serversystem.ServerSystem.Companion.log

class PlayerStateMigrator : AbstractMigrator() {
    override fun migrateFrom(): Int {
        var count = 0

        val userManager = userManager
        val essentials = essentials
        val uuids = essentials.users.allUserUUIDs

        for (uuid in uuids) {
            val userOptional = userManager.getUserOrNull(uuid)
            if (userOptional == null) {
                log.warning("Couldn't find user '${uuid}', skipping state migration!")
                continue
            }

            val essentialsUser = essentials.getUser(uuid)
            val user = userOptional.offlineUser

            user.isAcceptsMessages = !essentialsUser.isIgnoreMsg
            user.isSocialSpyEnabled = essentialsUser.isSocialSpyEnabled

            user.isAcceptsTeleports = essentialsUser.isTeleportEnabled

            user.isGodMode = essentialsUser.isGodModeEnabled
            user.isVanish = essentialsUser.isVanished

            user.logoutPosition = essentialsUser.logoutLocation

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
                log.warning("Couldn't find user '${uuid}', skipping state migration!")
                continue
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

            count += 1
        }

        return count
    }
}
