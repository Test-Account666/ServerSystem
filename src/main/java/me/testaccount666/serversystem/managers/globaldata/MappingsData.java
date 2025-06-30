package me.testaccount666.serversystem.managers.globaldata;

import me.testaccount666.serversystem.managers.config.ConfigReader;
import org.bukkit.Bukkit;

import java.util.*;

public class MappingsData {
    private static GameMode _GameMode;

    public static void initialize(ConfigReader config) {
        _GameMode = new GameMode(config);
    }

    public static GameMode GameMode() {
        return _GameMode;
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
}
