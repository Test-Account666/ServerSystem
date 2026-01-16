package me.testaccount666.serversystem.managers.messages

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.managers.config.ConfigReader
import me.testaccount666.serversystem.managers.config.DefaultConfigReader
import org.bukkit.plugin.Plugin
import java.nio.file.Path
import java.util.Locale.getDefault

class LanguageLoader {
    private val _baseDirectory: Path = Path.of("plugins", "ServerSystem", "messages")
    private val _languageMessagesMap: MutableMap<String, ConfigReader> = HashMap()
    private val _languageMappingsMap: MutableMap<String, ConfigReader> = HashMap()

    init {
        val plugin = ServerSystem.instance
        ensureExists(plugin, "english")
        ensureExists(plugin, "german")
        ensureExists(plugin, "slovene")
    }

    private fun ensureExists(plugin: Plugin, language: String) {
        val englishDirectory = _baseDirectory.resolve(language)

        val mappingsFile = englishDirectory.resolve("mappings.yml").toFile()
        if (!mappingsFile.exists()) plugin.saveResource("messages/${language}/mappings.yml", false)

        val messagesFile = englishDirectory.resolve("messages.yml").toFile()
        if (!messagesFile.exists()) plugin.saveResource("messages/${language}/messages.yml", false)
    }

    fun getMessageReader(language: String): ConfigReader? {
        val lowerLanguage = language.lowercase(getDefault())

        val configReader = _languageMessagesMap[lowerLanguage]
        if (configReader != null) return configReader

        val loadedReader = loadMessageReader(lowerLanguage) ?: return null

        _languageMessagesMap[lowerLanguage] = loadedReader

        return loadedReader
    }

    private fun loadMessageReader(language: String): ConfigReader? {
        val languageDirectory = _baseDirectory.resolve(language).toFile()
        if (!languageDirectory.exists() || !languageDirectory.isDirectory) {
            ServerSystem.log.warning("Requested language '${language}', but doesn't exist!")
            return null
        }
        val messageFile = languageDirectory.toPath().resolve("messages.yml").toFile()
        if (!messageFile.exists()) {
            ServerSystem.log.warning("Requested message language '${language}', but doesn't exist!")
            return null
        }

        return DefaultConfigReader.loadConfiguration(messageFile)
    }


    fun getMappingReader(language: String): ConfigReader? {
        val lowerLanguage = language.lowercase(getDefault())

        val configReader = _languageMappingsMap[lowerLanguage]
        if (configReader != null) return configReader

        val loadedReader = loadMappingReader(lowerLanguage) ?: return null

        _languageMappingsMap[lowerLanguage] = loadedReader

        return loadedReader
    }

    private fun loadMappingReader(language: String): ConfigReader? {
        val languageDirectory = _baseDirectory.resolve(language).toFile()
        if (!languageDirectory.exists() || !languageDirectory.isDirectory) {
            ServerSystem.log.warning("Requested language '${language}', but doesn't exist!")
            return null
        }

        val mappingsFile = languageDirectory.toPath().resolve("mappings.yml").toFile()
        if (!mappingsFile.exists()) {
            ServerSystem.log.warning("Requested mapping language '${language}', but doesn't exist!")
            return null
        }

        return DefaultConfigReader.loadConfiguration(mappingsFile)
    }
}