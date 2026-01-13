package me.testaccount666.migration.plugins.essentials

import me.testaccount666.migration.plugins.PluginMigrator
import me.testaccount666.serversystem.ServerSystem.Companion.log
import org.bukkit.Bukkit
import java.util.logging.Level

class EssentialsMigrator : PluginMigrator {
    private val _warpMigrator: AbstractMigrator by lazy { WarpMigrator() }
    private val _homeMigrator: AbstractMigrator by lazy { HomeMigrator() }
    private val _balanceMigrator: AbstractMigrator by lazy { BalanceMigrator() }
    private val _stateMigrator: AbstractMigrator by lazy { PlayerStateMigrator() }
    private val _muteMigrator: AbstractMigrator by lazy { MuteMigrator() }

    override val plugin by lazy { Bukkit.getPluginManager().getPlugin("Essentials")!! }

    override fun migrateFrom() {
        val logger = log
        logger.info("Starting migration of Essentials data!")

        try {
            val warpCount = _warpMigrator.migrateFrom()
            logger.info("Migrated ${warpCount} warps!")

            val homeCount = _homeMigrator.migrateFrom()
            logger.info("Migrated ${homeCount} homes!")

            val balanceCount = _balanceMigrator.migrateFrom()
            logger.info("Migrated ${balanceCount} balances!")

            val stateCount = _stateMigrator.migrateFrom()
            logger.info("Migrated ${stateCount} player states!")

            val muteCount = _muteMigrator.migrateFrom()
            logger.info("Migrated ${muteCount} mutes!")

            logger.info("Finished migration of Essentials data!")
        } catch (throwable: Throwable) {
            logger.log(Level.SEVERE, "An error occurred while migrating Essentials data!", throwable)
        }
    }

    override fun migrateTo() {
        val logger = log
        logger.info("Starting migration to Essentials!")

        try {
            val warpCount = _warpMigrator.migrateTo()
            logger.info("Migrated ${warpCount} warps!")

            val homeCount = _homeMigrator.migrateTo()
            logger.info("Migrated ${homeCount} homes!")

            val balanceCount = _balanceMigrator.migrateTo()
            logger.info("Migrated ${balanceCount} balances!")

            val stateCount = _stateMigrator.migrateTo()
            logger.info("Migrated ${stateCount} player states!")

            val muteCount = _muteMigrator.migrateTo()
            logger.info("Migrated ${muteCount} mutes!")

            logger.info("Finished migration to Essentials!")
        } catch (throwable: Throwable) {
            logger.log(Level.SEVERE, "An error occurred while migrating to Essentials!", throwable)
        }
    }
}
