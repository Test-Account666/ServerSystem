package me.testaccount666.migration.plugins.essentials

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.executables.warp.manager.WarpManager
import java.util.logging.Level

class WarpMigrator : AbstractMigrator() {
    override fun migrateFrom(): Int {
        val warpManager = instance.registry.getService<WarpManager>()
        val essentials = essentials

        var count = 0
        for (warpName in essentials.warps.list) try {
            val location = essentials.warps.getWarp(warpName)
            warpManager.addWarp(warpName, location)

            count += 1
        } catch (exception: Exception) {
            log.log(Level.WARNING, "Couldn't migrate warp '${warpName}'", exception)
        }

        return count
    }

    override fun migrateTo(): Int {
        val warpManager = instance.registry.getService<WarpManager>()
        val essentials = essentials

        var count = 0
        for (warp in warpManager.warps) try {
            val warpName = warp.displayName
            val location = warp.location

            essentials.warps.setWarp(warpName, location)

            count += 1
        } catch (exception: Exception) {
            log.log(Level.WARNING, "Couldn't migrate warp '${warp.displayName}'", exception)
        }

        return count
    }
}
