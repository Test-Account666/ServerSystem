package me.testaccount666.serversystem.managers.messages

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.commands.executables.back.CommandBack
import me.testaccount666.serversystem.managers.config.ConfigReader
import me.testaccount666.serversystem.userdata.OfflineUser

class MappingsData {
    private val _gameModeByLanguage = HashMap<String, GameMode>()
    private val _messageColorsByLanguage = HashMap<String, MessageColors>()
    private val _backTypeByLanguage = HashMap<String, BackType>()
    private val _moderationByLanguage = HashMap<String, Moderation>()
    private val _consoleByLanguage = HashMap<String, Console>()

    init {
        _Instance = this
    }

    class BackType(config: ConfigReader) {
        private val _backTypeMappings = HashMap<CommandBack.BackType, String>()

        init {
            for (backType in CommandBack.BackType.entries.toTypedArray()) {
                val backTypeName = config.getString("Mappings.BackType.${backType.name.lowercase()}") ?: run {
                    ServerSystem.log.warning("BackType mapping for '${backType.name}' is not defined in the config!")
                    continue
                }

                _backTypeMappings[backType] = backTypeName
            }
        }

        fun getBackTypeName(backType: CommandBack.BackType) = _backTypeMappings[backType]
    }

    class GameMode {
        constructor(config: ConfigReader) {
            for (gameMode in org.bukkit.GameMode.entries.toTypedArray()) {
                val gameModeName = config.getString("Mappings.GameMode.${gameMode.name.lowercase()}") ?: run {
                    ServerSystem.log.warning("GameMode mapping for '${gameMode.name}' is not defined in the config!")
                    continue
                }

                _gameModeMappings[gameMode] = gameModeName
            }
        }

        private val _gameModeMappings = HashMap<org.bukkit.GameMode, String>()

        fun getGameModeName(gameMode: org.bukkit.GameMode) = _gameModeMappings[gameMode]

        val gameModeNames
            get() = _gameModeMappings.values.toSet()
    }

    class MessageColors(config: ConfigReader) {
        private val _messageColorMappings = HashMap<String, String>()

        init {
            val prefixColor = config.getString("Mappings.MessageColors.prefix")
            val separatorColor = config.getString("Mappings.MessageColors.separators")
            val messageColor = config.getString("Mappings.MessageColors.message")
            val highlightColor = config.getString("Mappings.MessageColors.highlight")
            val errorMessageColor = config.getString("Mappings.MessageColors.error.message")
            val errorHighlightColor = config.getString("Mappings.MessageColors.error.highlight")

            requireNotNull(prefixColor) { "prefix color missing" }
            requireNotNull(separatorColor) { "separator color missing" }
            requireNotNull(messageColor) { "message color missing" }
            requireNotNull(highlightColor) { "highlight_color missing" }
            requireNotNull(errorMessageColor) { "message color missing" }
            requireNotNull(errorHighlightColor) { "error_highlight_color missing" }

            _messageColorMappings["Prefix"] = prefixColor
            _messageColorMappings["Separator"] = separatorColor
            _messageColorMappings["Message"] = messageColor
            _messageColorMappings["Highlight"] = highlightColor
            _messageColorMappings["ErrorMessage"] = errorMessageColor
            _messageColorMappings["ErrorHighlight"] = errorHighlightColor
        }

        /**
         * Gets the message color as a legacy String with color codes.
         *
         * @param colorId The color identifier
         * @return The color String, or null if not found
         */
        fun getMessageColor(colorId: String) = _messageColorMappings[colorId]
    }

    class Moderation {
        constructor(config: ConfigReader) {
            val neverName = config.getString("Mappings.Moderation.Permanent") ?: run {
                ServerSystem.log.warning("Moderation mapping for 'Permanent' is not defined in the config!")
                return
            }
            _moderationMappings["permanent"] = neverName
        }

        private val _moderationMappings = HashMap<String, String>()

        fun getName(key: String) = _moderationMappings[key]
    }

    class Console {
        constructor(config: ConfigReader) {
            val neverName = config.getString("Mappings.Console.Name") ?: run {
                ServerSystem.log.warning("Console mapping for 'Name' is not defined in the config!")
                return
            }

            _consoleMappings["name"] = neverName
        }

        private val _consoleMappings = HashMap<String, String>()

        fun getName(key: String) = _consoleMappings[key]
    }

    companion object {
        private lateinit var _Instance: MappingsData
        private fun <T> getOrLoadMappingData(user: OfflineUser, cache: MutableMap<String, T>, factory: (ConfigReader) -> T): T {
            return getOrLoadMappingData(user.playerLanguage, cache, factory)
        }

        private fun <T> getOrLoadMappingData(language: String, cache: MutableMap<String, T>, factory: (ConfigReader) -> T): T {
            cache[language]?.let { return it }

            MessageManager.languageLoader.getMappingReader(language)?.let { reader ->
                return factory(reader).also { cache[language] = it }
            }

            val reader = MessageManager.languageLoader.getMappingReader(MessageManager.defaultLanguage)!!
            return factory(reader).also { cache[language] = it }
        }

        /**
         * Gets the Moderation mapping data for the given user
         *
         * @param user The user to get the language from
         * @return The Moderation mapping data
         */
        fun moderation(user: OfflineUser): Moderation {
            return getOrLoadMappingData(user, _Instance._moderationByLanguage) { Moderation(it) }
        }

        /**
         * Gets the Console mapping data for the given user
         *
         * @param user The user to get the language from
         * @return The Console mapping data
         */
        fun console(user: OfflineUser): Console {
            return getOrLoadMappingData(user, _Instance._consoleByLanguage) { Console(it) }
        }

        /**
         * Gets the GameMode mapping data for the given user
         *
         * @param user The user to get the language from
         * @return The GameMode mapping data
         */
        @JvmStatic
        fun gameMode(user: OfflineUser): GameMode {
            return getOrLoadMappingData(user, _Instance._gameModeByLanguage) { GameMode(it) }
        }

        /**
         * Gets the MessageColors mapping data for the given user
         *
         * @param user The user to get the language from
         * @return The MessageColors mapping data
         */
        @JvmStatic
        fun messageColors(user: OfflineUser): MessageColors {
            return getOrLoadMappingData(user, _Instance._messageColorsByLanguage) { MessageColors(it) }
        }

        /**
         * Gets the BackType mapping data for the given user
         *
         * @param user The user to get the language from
         * @return The BackType mapping data
         */
        @JvmStatic
        fun backType(user: OfflineUser): BackType {
            return getOrLoadMappingData(user, _Instance._backTypeByLanguage) { BackType(it) }
        }

        fun instance() = _Instance
    }
}