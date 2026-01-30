package me.testaccount666.migration.plugins.essentials

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.executables.waypoints.warp.manager.WarpManager
import java.util.logging.Level

class WarpMigrator : AbstractMigrator() {
    val warpManager by lazy { instance.registry.getService<WarpManager>() }
    override fun migrateFrom(): Int {
        val essentials = essentials

        val count = essentials.warps.list.count { warpName ->
            runCatching {
                val location = essentials.warps.getWarp(warpName)
                warpManager.addPoint(warpName, location)

                return@runCatching true
            }.onFailure { log.log(Level.WARNING, "Couldn't migrate warp '$warpName'", it) }.getOrDefault(false)
        }

        return count
    }

    override fun migrateTo(): Int {
        val essentials = essentials

        val count = warpManager.waypoints.count { warp ->
            runCatching {
                val warpName = warp.displayName
                val location = warp.location

                essentials.warps.setWarp(warpName, location)

                return@runCatching true
            }.onFailure { log.log(Level.WARNING, "Couldn't migrate warp '${warp.displayName}'", it) }.getOrDefault(false)
        }

        return count
    }
}
