package me.testaccount666.serversystem.managers.globaldata;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.testaccount666.serversystem.commands.executables.back.CommandBack;
import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.utils.ChatColor;
import org.bukkit.Bukkit;

import java.util.*;

@Accessors(fluent = true)
public class MappingsData {
    @Getter
    private static GameMode _GameMode;
    @Getter
    private static MessageColors _MessageColors;
    @Getter
    private static BackType _BackType;
    @Getter
    private static Moderation _Moderation;

    public static void initialize(ConfigReader config) {
        _GameMode = new GameMode(config);
        _MessageColors = new MessageColors(config);
        _BackType = new BackType(config);
        _Moderation = new Moderation(config);
    }

    public static class BackType {
        private final Map<CommandBack.BackType, String> _backTypeMappings = new HashMap<>();

        public BackType(ConfigReader config) {
            for (var backType : CommandBack.BackType.values()) {
                var backTypeName = config.getString("Mappings.BackType.${backType.name().toLowerCase()}");

                if (backTypeName == null) {
                    Bukkit.getLogger().warning("BackType mapping for '${backType.name()}' is not defined in the config!");
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
                    Bukkit.getLogger().warning("GameMode mapping for '${gameMode.name()}' is not defined in the config!");
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
            var prefixColor = ChatColor.translateColorCodes(config.getString("Mappings.MessageColors.prefix"));
            var separatorColor = ChatColor.translateColorCodes(config.getString("Mappings.MessageColors.separators"));
            var messageColor = ChatColor.translateColorCodes(config.getString("Mappings.MessageColors.message"));
            var highlightColor = ChatColor.translateColorCodes(config.getString("Mappings.MessageColors.highlight"));
            var errorMessageColor = ChatColor.translateColorCodes(config.getString("Mappings.MessageColors.error.message"));
            var errorHighlightColor = ChatColor.translateColorCodes(config.getString("Mappings.MessageColors.error.highlight"));

            _messageColorMappings.put("Prefix", prefixColor);
            _messageColorMappings.put("Separator", separatorColor);
            _messageColorMappings.put("Message", messageColor);
            _messageColorMappings.put("Highlight", highlightColor);
            _messageColorMappings.put("ErrorMessage", errorMessageColor);
            _messageColorMappings.put("ErrorHighlight", errorHighlightColor);
        }

        public Optional<String> getMessageColor(String colorId) {
            return Optional.ofNullable(_messageColorMappings.getOrDefault(colorId, null));
        }
    }

    public static class Moderation {
        private final Map<String, String> _moderationMappings = new HashMap<>();

        public Moderation(ConfigReader config) {
            var neverName = config.getString("Mappings.Moderation.Permanent");

            if (neverName == null) {
                Bukkit.getLogger().warning("Moderation mapping for 'Permanent' is not defined in the config!");
                return;
            }

            _moderationMappings.put("permanent", neverName);
        }

        public Optional<String> getName(String key) {
            return Optional.ofNullable(_moderationMappings.getOrDefault(key, null));
        }
    }
}
