package me.testaccount666.migration.essentials;

import org.bukkit.Bukkit;

import java.util.function.Function;

//TODO: Finish migrators, there's more data to migrate
public class EssentialsMigrator {
    private final Function<Void, AbstractMigrator> _warpMigrator = (var ignored) -> new WarpMigrator();
    private final Function<Void, AbstractMigrator> _homeMigrator = (var ignored) -> new HomeMigrator();
    private final Function<Void, AbstractMigrator> _balanceMigrator = (var ignored) -> new BalanceMigrator();
    private final Function<Void, AbstractMigrator> _stateMigrator = (var ignored) -> new PlayerStateMigrator();
    private final Function<Void, AbstractMigrator> _muteMigrator = (var ignored) -> new MuteMigrator();

    public boolean isEssentialsInstalled() {
        return Bukkit.getPluginManager().getPlugin("Essentials") != null;
    }

    public void migrateFrom() {
        var warpCount = _warpMigrator.apply(null).migrateFrom();
        var homeCount = _homeMigrator.apply(null).migrateFrom();
        var balanceCount = _balanceMigrator.apply(null).migrateFrom();
        var stateCount = _stateMigrator.apply(null).migrateFrom();
        var muteCount = _muteMigrator.apply(null).migrateFrom();
    }

    public void migrateTo() {
        var warpCount = _warpMigrator.apply(null).migrateTo();
        var homeCount = _homeMigrator.apply(null).migrateTo();
        var balanceCount = _balanceMigrator.apply(null).migrateTo();
        var stateCount = _stateMigrator.apply(null).migrateTo();
        var muteCount = _muteMigrator.apply(null).migrateTo();
    }
}
