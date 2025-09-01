package me.testaccount666.migration.plugins;

import io.github.classgraph.ClassGraph;
import me.testaccount666.serversystem.ServerSystem;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

public class MigratorRegistry {
    private final Map<String, PluginMigrator> _migrators = new HashMap<>();

    public void registerMigrators() {
        try (var scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptPackages("me.testaccount666.migration.plugins")
                .scan()) {
            var migratorClasses = scanResult.getClassesImplementing(PluginMigrator.class).loadClasses();
            for (var migratorClass : migratorClasses)
                try {
                    var migrator = (PluginMigrator) migratorClass.getConstructor().newInstance();
                    var plugin = migrator.getPlugin();
                    if (plugin == null) continue;

                    _migrators.put(plugin.getName().toLowerCase(), migrator);
                } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException exception) {
                    ServerSystem.getLog().log(Level.WARNING, "Failed to register migrator '${migratorClass.getName()}'", exception);
                }
        }
    }

    public Optional<PluginMigrator> getMigrator(String pluginName) {
        return Optional.ofNullable(_migrators.get(pluginName.toLowerCase()));
    }

    public Set<String> getMigrators() {
        return _migrators.keySet();
    }
}
