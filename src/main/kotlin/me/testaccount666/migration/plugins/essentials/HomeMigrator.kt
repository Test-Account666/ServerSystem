package me.testaccount666.migration.plugins.essentials

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.userdata.OfflineUser
import java.util.logging.Level

class HomeMigrator : AbstractMigrator() {
    override fun migrateFrom(): Int {
        var count = 0

        val userManager = userManager
        val essentials = essentials
        val uuids = essentials.users.allUserUUIDs

        for (uuid in uuids) {
            val cachedUser = userManager.getUserOrNull(uuid) ?: run {
                log.warning("Couldn't find user '${uuid}', skipping home migration!")
                continue
            }

            count += migrateFrom(cachedUser.offlineUser)
        }

        return count
    }

    private fun migrateFrom(user: OfflineUser): Int {
        var count = 0

        val homeManager = user.homeManager
        val essentials = essentials

        val essentialsUser = essentials.getUser(user.uuid)
        for (homeName in essentialsUser.homes) try {
            val location = essentialsUser.getHome(homeName)

            homeManager.addPoint(homeName, location)
            count += 1
        } catch (exception: Exception) {
            val userName = user.getNameSafe()

            log.log(Level.WARNING, "Couldn't migrate home '${homeName}' for user '${user.uuid}' (${userName})", exception)
        }

        user.save()

        return count
    }

    override fun migrateTo(): Int {
        var count = 0

        val userManager = userManager

        for (player in offlinePlayers()) {
            val uuid = player.uniqueId

            val cachedUser = userManager.getUserOrNull(uuid) ?: run {
                log.warning("Couldn't find user '${uuid}', skipping home migration!")
                continue
            }

            count += migrateTo(cachedUser.offlineUser)
        }

        return count
    }

    private fun migrateTo(user: OfflineUser): Int {
        var count = 0

        val homeManager = user.homeManager
        val essentials = essentials

        ensureUserDataExists(user.uuid)
        val essentialsUser = essentials.getUser(user.uuid)

        for (home in homeManager.waypoints) {
            val homeName = home.displayName
            val location = home.location

            try {
                essentialsUser.setHome(homeName, location)

                count += 1
            } catch (exception: Exception) {
                val userName = user.getNameSafe()

                log.log(Level.WARNING, "Couldn't migrate home '${homeName}' for user '${user.uuid}' (${userName})", exception)
            }
        }

        return count
    }
}
