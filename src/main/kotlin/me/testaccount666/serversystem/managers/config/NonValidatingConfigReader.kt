package me.testaccount666.serversystem.managers.config

import me.testaccount666.serversystem.ServerSystem
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.logging.Level

class NonValidatingConfigReader(file: File, plugin: Plugin) : DefaultConfigReader(file, plugin) {
    override fun validateAndFixConfig() = true

    override fun loadDefaultConfig() {}

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