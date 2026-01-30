package me.testaccount666.migration.plugins

import io.github.classgraph.ClassGraph
import me.testaccount666.serversystem.ServerSystem.Companion.log
import java.util.logging.Level

class MigratorRegistry {
    private val _migrators = HashMap<String, PluginMigrator>()

    fun registerMigrators() {
        ClassGraph()
            .enableAllInfo()
            .acceptPackages("me.testaccount666.migration.plugins")
            .scan().use { scanResult ->
                val migratorClasses = scanResult.getClassesImplementing(PluginMigrator::class.java).loadClasses()
                for (migratorClass in migratorClasses) try {
                    val migrator = migratorClass.getConstructor().newInstance() as PluginMigrator
                    val plugin = migrator.plugin ?: continue

                    _migrators[plugin.name.lowercase()] = migrator
                } catch (exception: Exception) {
                    log.log(Level.WARNING, "Failed to register migrator '${migratorClass.name}'", exception)
                }
            }
    }

    fun getMigrator(pluginName: String) = _migrators[pluginName.lowercase()]

    val migrators
        get() = _migrators.keys.toSet()
}
