package me.testaccount666.migration.plugins

import io.github.classgraph.ClassGraph
import me.testaccount666.serversystem.ServerSystem.Companion.log
import java.util.Locale.getDefault
import java.util.logging.Level

class MigratorRegistry {
    private val _migrators: MutableMap<String, PluginMigrator> = HashMap()

    fun registerMigrators() {
        ClassGraph()
            .enableAllInfo()
            .acceptPackages("me.testaccount666.migration.plugins")
            .scan().use { scanResult ->
                val migratorClasses = scanResult.getClassesImplementing(PluginMigrator::class.java).loadClasses()
                for (migratorClass in migratorClasses) try {
                    val migrator = migratorClass.getConstructor().newInstance() as PluginMigrator
                    val plugin = migrator.plugin ?: continue

                    _migrators[plugin.name.lowercase(getDefault())] = migrator
                } catch (exception: Exception) {
                    log.log(Level.WARNING, "Failed to register migrator '${migratorClass.name}'", exception)
                }
            }
    }

    fun getMigrator(pluginName: String) = _migrators[pluginName.lowercase(getDefault())]

    val migrators
        get() = _migrators.keys.toSet()
}
