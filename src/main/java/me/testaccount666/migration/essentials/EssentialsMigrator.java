package me.testaccount666.migration.essentials;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Function;

//TODO: Finish migrators, there's more data to migrate
public class EssentialsMigrator {
    private final Function<Void, WarpMigrator> _warpMigrator = (var ignored) -> new WarpMigrator();
    private final Function<Void, HomeMigrator> _homeMigrator = (var ignored) -> new HomeMigrator();
    private final Function<Void, BalanceMigrator> _balanceMigrator = (var ignored) -> new BalanceMigrator();

    @SneakyThrows
    static void ensureUserDataExists(UUID uuid) {
        var userDataDirectory = Path.of("plugins", "Essentials", "userdata");
        if (!userDataDirectory.toFile().exists()) userDataDirectory.toFile().mkdirs();

        var userFile = userDataDirectory.resolve("${uuid}.yml").toFile();
        if (!userFile.exists()) userFile.createNewFile();
    }

    public boolean isEssentialsInstalled() {
        return Bukkit.getPluginManager().getPlugin("Essentials") != null;
    }

    public void migrateFrom() {
        var warpCount = _warpMigrator.apply(null).migrateFrom();
        var homeCount = _homeMigrator.apply(null).migrateFrom();
        var balanceCount = _balanceMigrator.apply(null).migrateFrom();
    }

    public void migrateTo() {
        var warpCount = _warpMigrator.apply(null).migrateTo();
        var homeCount = _homeMigrator.apply(null).migrateTo();
        var balanceCount = _balanceMigrator.apply(null).migrateTo();
    }
}
