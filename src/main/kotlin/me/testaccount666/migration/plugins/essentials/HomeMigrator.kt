package me.testaccount666.migration.plugins.essentials

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.userdata.OfflineUser
import java.util.logging.Level

class HomeMigrator : AbstractMigrator() {
    override fun migrateFrom(): Int {
        val count = essentials.users.allUserUUIDs.sumOf {
            val cachedUser = userManager.getUserOrNull(it) ?: run {
                log.warning("Couldn't find user '${it}', skipping home migration!")
                return@sumOf 0
            }

            return@sumOf migrateFrom(cachedUser.offlineUser)
        }

        return count
    }

    private fun migrateFrom(user: OfflineUser): Int {
        val essentialsUser = essentials.getUser(user.uuid)
        val count = essentialsUser.homes.count { homeName ->
            runCatching {
                val location = essentialsUser.getHome(homeName)

                user.homeManager.addPoint(homeName, location)
                return@runCatching true
            }.onFailure {
                log.log(Level.WARNING, "Couldn't migrate home '${homeName}' for user '${user.uuid}' (${user.nameSafe})", it)
            }.getOrDefault(false)
        }

        user.save()

        return count
    }

    override fun migrateTo(): Int {
        val count = offlinePlayers().sumOf {
            val uuid = it.uniqueId

            val cachedUser = userManager.getUserOrNull(uuid) ?: run {
                log.warning("Couldn't find user '${uuid}', skipping home migration!")
                return@sumOf 0
            }

            return@sumOf migrateTo(cachedUser.offlineUser)
        }

        return count
    }

    private fun migrateTo(user: OfflineUser): Int {
        ensureUserDataExists(user.uuid)
        val essentialsUser = essentials.getUser(user.uuid)

        val count = user.homeManager.waypoints.count { home ->
            runCatching {
                val homeName = home.displayName
                val location = home.location
                essentialsUser.setHome(homeName, location)
                return@runCatching true
            }.onFailure {
                log.log(Level.WARNING, "Couldn't migrate home '${home.displayName}' for user '${user.uuid}' (${user.nameSafe})", it)
            }.getOrDefault(false)
        }

        return count
    }
}
