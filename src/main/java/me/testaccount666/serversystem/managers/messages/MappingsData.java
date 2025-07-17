package me.testaccount666.serversystem.managers.messages;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.executables.back.CommandBack;
import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.userdata.OfflineUser;

import java.util.*;
import java.util.function.Function;

@Accessors(fluent = true)
public class MappingsData {
    @Getter
    private static MappingsData _Instance;
    private final Map<String, GameMode> _gameModeByLanguage = new HashMap<>();
    private final Map<String, MessageColors> _messageColorsByLanguage = new HashMap<>();
    private final Map<String, BackType> _backTypeByLanguage = new HashMap<>();
    private final Map<String, Moderation> _moderationByLanguage = new HashMap<>();
    private final Map<String, Console> _consoleByLanguage = new HashMap<>();

    public MappingsData() {
        _Instance = this;
    }

    private static <T> T getOrLoadMappingData(OfflineUser user, Map<String, T> cache, Function<ConfigReader, T> factory) {
        var language = user.getPlayerLanguage();
        return getOrLoadMappingData(language, cache, factory);
    }

    private static <T> T getOrLoadMappingData(String language, Map<String, T> cache, Function<ConfigReader, T> factory) {
        var data = cache.get(language);
        if (data != null) return data;

        var readerOptional = MessageManager.getLanguageLoader().getMappingReader(language);
        if (readerOptional.isPresent()) {
            data = factory.apply(readerOptional.get());
            cache.put(language, data);
            return data;
        }

        readerOptional = MessageManager.getLanguageLoader().getMappingReader("english");
        data = factory.apply(readerOptional.get());
        cache.put(language, data);
        return data;
    }

    /**
     * Static method to get the GameMode mapping data using the default language
     *
     * @return The GameMode mapping data
     */
    public static GameMode gameMode() {
        return getOrLoadMappingData("english", _Instance._gameModeByLanguage, GameMode::new);
    }

    /**
     * Static method to get the MessageColors mapping data using the default language
     *
     * @return The MessageColors mapping data
     */
    public static MessageColors messageColors() {
        return getOrLoadMappingData("english", _Instance._messageColorsByLanguage, MessageColors::new);
    }

    /**
     * Static method to get the BackType mapping data using the default language
     *
     * @return The BackType mapping data
     */
    public static BackType backType() {
        return getOrLoadMappingData("english", _Instance._backTypeByLanguage, BackType::new);
    }

    /**
     * Static method to get the Moderation mapping data using the default language
     *
     * @return The Moderation mapping data
     */
    public static Moderation moderation() {
        return getOrLoadMappingData("english", _Instance._moderationByLanguage, Moderation::new);
    }

    /**
     * Static method to get the Console mapping data using the default language
     *
     * @return The Console mapping data
     */
    public static Console console() {
        return getOrLoadMappingData("english", _Instance._consoleByLanguage, Console::new);
    }

    /**
     * Gets the Moderation mapping data for the given user
     *
     * @param user The user to get the language from
     * @return The Moderation mapping data
     */
    public static Moderation moderation(OfflineUser user) {
        return getOrLoadMappingData(user, _Instance._moderationByLanguage, Moderation::new);
    }

    /**
     * Gets the Console mapping data for the given user
     *
     * @param user The user to get the language from
     * @return The Console mapping data
     */
    public static Console console(OfflineUser user) {
        return getOrLoadMappingData(user, _Instance._consoleByLanguage, Console::new);
    }

    /**
     * Gets the GameMode mapping data for the given user
     *
     * @param user The user to get the language from
     * @return The GameMode mapping data
     */
    public static GameMode gameMode(OfflineUser user) {
        return getOrLoadMappingData(user, _Instance._gameModeByLanguage, GameMode::new);
    }

    /**
     * Gets the MessageColors mapping data for the given user
     *
     * @param user The user to get the language from
     * @return The MessageColors mapping data
     */
    public static MessageColors messageColors(OfflineUser user) {
        return getOrLoadMappingData(user, _Instance._messageColorsByLanguage, MessageColors::new);
    }

    /**
     * Gets the BackType mapping data for the given user
     *
     * @param user The user to get the language from
     * @return The BackType mapping data
     */
    public static BackType backType(OfflineUser user) {
        return getOrLoadMappingData(user, _Instance._backTypeByLanguage, BackType::new);
    }

    public static class BackType {
        private final Map<CommandBack.BackType, String> _backTypeMappings = new HashMap<>();

        public BackType(ConfigReader config) {
            for (var backType : CommandBack.BackType.values()) {
                var backTypeName = config.getString("Mappings.BackType.${backType.name().toLowerCase()}");

                if (backTypeName == null) {
                    ServerSystem.getLog().warning("BackType mapping for '${backType.name()}' is not defined in the config!");
                    continue;
                }

                _backTypeMappings.put(backType, backTypeName);
            }
        }

        public Optional<String> getBackTypeName(CommandBack.BackType backType) {
            return Optional.ofNullable(_backTypeMappings.getOrDefault(backType, null));
        }
    }

    public static class GameMode {
        private final Map<org.bukkit.GameMode, String> _gameModeMappings = new HashMap<>();

        public GameMode(ConfigReader config) {
            for (var gameMode : org.bukkit.GameMode.values()) {
                var gameModeName = config.getString("Mappings.GameMode.${gameMode.name().toLowerCase()}");

                if (gameModeName == null) {
                    ServerSystem.getLog().warning("GameMode mapping for '${gameMode.name()}' is not defined in the config!");
                    continue;
                }

                _gameModeMappings.put(gameMode, gameModeName);
            }
        }

        public Optional<String> getGameModeName(org.bukkit.GameMode gameMode) {
            return Optional.ofNullable(_gameModeMappings.getOrDefault(gameMode, null));
        }

        public Set<String> getGameModeNames() {
            return new HashSet<>(_gameModeMappings.values());
        }
    }

    public static class MessageColors {
        private final Map<String, String> _messageColorMappings = new HashMap<>();

        public MessageColors(ConfigReader config) {
            var prefixColor = config.getString("Mappings.MessageColors.prefix");
            var separatorColor = config.getString("Mappings.MessageColors.separators");
            var messageColor = config.getString("Mappings.MessageColors.message");
            var highlightColor = config.getString("Mappings.MessageColors.highlight");
            var errorMessageColor = config.getString("Mappings.MessageColors.error.message");
            var errorHighlightColor = config.getString("Mappings.MessageColors.error.highlight");

            _messageColorMappings.put("Prefix", prefixColor);
            _messageColorMappings.put("Separator", separatorColor);
            _messageColorMappings.put("Message", messageColor);
            _messageColorMappings.put("Highlight", highlightColor);
            _messageColorMappings.put("ErrorMessage", errorMessageColor);
            _messageColorMappings.put("ErrorHighlight", errorHighlightColor);
        }

        /**
         * Gets the message color as a legacy String with color codes.
         *
         * @param colorId The color identifier
         * @return An Optional containing the color String, or empty if not found
         */
        public Optional<String> getMessageColor(String colorId) {
            return Optional.ofNullable(_messageColorMappings.getOrDefault(colorId, null));
        }
    }

    public static class Moderation {
        private final Map<String, String> _moderationMappings = new HashMap<>();

        public Moderation(ConfigReader config) {
            var neverName = config.getString("Mappings.Moderation.Permanent");

            if (neverName == null) {
                ServerSystem.getLog().warning("Moderation mapping for 'Permanent' is not defined in the config!");
                return;
            }

            _moderationMappings.put("permanent", neverName);
        }

        public Optional<String> getName(String key) {
            return Optional.ofNullable(_moderationMappings.getOrDefault(key, null));
        }
    }

    public static class Console {
        private final Map<String, String> _consoleMappings = new HashMap<>();

        public Console(ConfigReader config) {
            var neverName = config.getString("Mappings.Console.Name");

            if (neverName == null) {
                ServerSystem.getLog().warning("Console mapping for 'Name' is not defined in the config!");
                return;
            }

            _consoleMappings.put("name", neverName);
        }

        public Optional<String> getName(String key) {
            return Optional.ofNullable(_consoleMappings.getOrDefault(key, null));
        }
    }
}
