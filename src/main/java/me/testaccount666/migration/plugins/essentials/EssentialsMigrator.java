package me.testaccount666.migration.plugins.essentials;

import me.testaccount666.migration.plugins.PluginMigrator;
import me.testaccount666.serversystem.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.function.Function;
import java.util.logging.Level;

public class EssentialsMigrator implements PluginMigrator {
    private final Function<Void, AbstractMigrator> _warpMigrator = (var ignored) -> new WarpMigrator();
    private final Function<Void, AbstractMigrator> _homeMigrator = (var ignored) -> new HomeMigrator();
    private final Function<Void, AbstractMigrator> _balanceMigrator = (var ignored) -> new BalanceMigrator();
    private final Function<Void, AbstractMigrator> _stateMigrator = (var ignored) -> new PlayerStateMigrator();
    private final Function<Void, AbstractMigrator> _muteMigrator = (var ignored) -> new MuteMigrator();

    @Override
    public Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("Essentials");
    }

    @Override
    public void migrateFrom() {
        var logger = ServerSystem.getLog();
        logger.info("Starting migration of Essentials data!");

        try {
            var warpCount = _warpMigrator.apply(null).migrateFrom();
            logger.info("Migrated ${warpCount} warps!");

            var homeCount = _homeMigrator.apply(null).migrateFrom();
            logger.info("Migrated ${homeCount} homes!");

            var balanceCount = _balanceMigrator.apply(null).migrateFrom();
            logger.info("Migrated ${balanceCount} balances!");

            var stateCount = _stateMigrator.apply(null).migrateFrom();
            logger.info("Migrated ${stateCount} player states!");

            var muteCount = _muteMigrator.apply(null).migrateFrom();
            logger.info("Migrated ${muteCount} mutes!");

            logger.info("Finished migration of Essentials data!");
        } catch (Throwable throwable) {
            logger.log(Level.SEVERE, "An error occurred while migrating Essentials data!", throwable);
        }
    }

    @Override
    public void migrateTo() {
        var logger = ServerSystem.getLog();
        logger.info("Starting migration to Essentials!");

        try {
            var warpCount = _warpMigrator.apply(null).migrateTo();
            logger.info("Migrated ${warpCount} warps!");

            var homeCount = _homeMigrator.apply(null).migrateTo();
            logger.info("Migrated ${homeCount} homes!");

            var balanceCount = _balanceMigrator.apply(null).migrateTo();
            logger.info("Migrated ${balanceCount} balances!");

            var stateCount = _stateMigrator.apply(null).migrateTo();
            logger.info("Migrated ${stateCount} player states!");

            var muteCount = _muteMigrator.apply(null).migrateTo();
            logger.info("Migrated ${muteCount} mutes!");

            logger.info("Finished migration to Essentials!");
        } catch (Throwable throwable) {
            logger.log(Level.SEVERE, "An error occurred while migrating to Essentials!", throwable);
        }
    }
}
