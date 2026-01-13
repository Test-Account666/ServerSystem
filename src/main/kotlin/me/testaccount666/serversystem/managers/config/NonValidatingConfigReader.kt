package me.testaccount666.serversystem.managers.config

import me.testaccount666.serversystem.ServerSystem
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.logging.Level

class NonValidatingConfigReader
/**
 * Creates a new DefaultConfigReader for the specified file and plugin.
 *
 * @param file   The configuration file to read
 * @param plugin The plugin associated with this configuration
 * @throws java.io.FileNotFoundException If the default configuration cannot be found
 */
    (file: File, plugin: Plugin) : DefaultConfigReader(file, plugin) {
    override fun validateAndFixConfig() = true

    override fun loadDefaultConfig() {
        // Nothing to do
    }

    override fun load(file: File?) {
        requireNotNull(file)

        try {
            _newReader = NonValidatingConfigReader(file, ServerSystem.instance)
        } catch (exception: Exception) {
            ServerSystem.log.log(Level.SEVERE, "Failed to load configuration file '${file.name}'", exception)
        }
    }

    companion object {
        fun loadConfiguration(file: File): ConfigReader = NonValidatingConfigReader(file, ServerSystem.instance)
    }
}