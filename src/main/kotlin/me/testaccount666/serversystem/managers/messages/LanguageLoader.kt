package me.testaccount666.serversystem.managers.messages

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.managers.config.ConfigReader
import me.testaccount666.serversystem.managers.config.DefaultConfigReader
import org.bukkit.plugin.Plugin

class LanguageLoader {
    private val _baseDirectory = instance.dataPath.resolve("messages")
    private val _languageMessagesMap: MutableMap<String, ConfigReader> = HashMap()
    private val _languageMappingsMap: MutableMap<String, ConfigReader> = HashMap()

    init {
        ensureExists(instance, "english")
        ensureExists(instance, "german")
        ensureExists(instance, "slovene")
    }

    private fun ensureExists(plugin: Plugin, language: String) {
        val englishDirectory = _baseDirectory.resolve(language)

        val mappingsFile = englishDirectory.resolve("mappings.yml").toFile()
        if (!mappingsFile.exists()) plugin.saveResource("messages/${language}/mappings.yml", false)

        val messagesFile = englishDirectory.resolve("messages.yml").toFile()
        if (!messagesFile.exists()) plugin.saveResource("messages/${language}/messages.yml", false)
    }

    fun getMessageReader(language: String): ConfigReader? {
        val lowerLanguage = language.lowercase()
        _languageMessagesMap[lowerLanguage]?.let { return it }

        return loadMessageReader(lowerLanguage)?.also { _languageMessagesMap[lowerLanguage] = it }
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
        val lowerLanguage = language.lowercase()
        _languageMappingsMap[lowerLanguage]?.let { return it }

        return loadMappingReader(lowerLanguage)?.also { _languageMappingsMap[lowerLanguage] = it }
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