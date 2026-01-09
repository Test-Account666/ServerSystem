package me.testaccount666.serversystem.managers.messages

import lombok.SneakyThrows
import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.managers.config.ConfigReader
import me.testaccount666.serversystem.managers.config.DefaultConfigReader
import org.bukkit.plugin.Plugin
import java.nio.file.Path
import java.util.*

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

    fun getMessageReader(language: String): Optional<ConfigReader> {
        var language = language
        language = language.lowercase(Locale.getDefault())

        val configReader = _languageMessagesMap[language]
        if (configReader != null) return Optional.of(configReader)

        val loadedReader = loadMessageReader(language) ?: return Optional.empty()

        _languageMessagesMap[language] = loadedReader

        return Optional.of(loadedReader)
    }

    @SneakyThrows
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


    fun getMappingReader(language: String): Optional<ConfigReader> {
        var language = language
        language = language.lowercase(Locale.getDefault())

        val configReader = _languageMappingsMap[language]
        if (configReader != null) return Optional.of(configReader)

        val loadedReader = loadMappingReader(language) ?: return Optional.empty()

        _languageMappingsMap[language] = loadedReader

        return Optional.of(loadedReader)
    }

    @SneakyThrows
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