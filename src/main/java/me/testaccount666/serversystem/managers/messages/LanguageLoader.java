package me.testaccount666.serversystem.managers.messages;

import lombok.SneakyThrows;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.config.DefaultConfigReader;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LanguageLoader {
    private final Path _baseDirectory = Path.of("plugins", "ServerSystem", "messages");
    private final Map<String, ConfigReader> _languageMessagesMap = new HashMap<>();
    private final Map<String, ConfigReader> _languageMappingsMap = new HashMap<>();

    public LanguageLoader() {
        var plugin = ServerSystem.Instance;
        ensureExists(plugin, "english");
        ensureExists(plugin, "german");
    }

    private void ensureExists(Plugin plugin, String language) {
        var englishDirectory = _baseDirectory.resolve(language);

        var mappingsFile = englishDirectory.resolve("mappings.yml").toFile();
        if (!mappingsFile.exists()) plugin.saveResource("messages/${language}/mappings.yml", false);

        var messagesFile = englishDirectory.resolve("messages.yml").toFile();
        if (!messagesFile.exists()) plugin.saveResource("messages/${language}/messages.yml", false);
    }

    public Optional<ConfigReader> getMessageReader(String language) {
        language = language.toLowerCase();
        var configReader = _languageMessagesMap.get(language);
        if (configReader != null) return Optional.of(configReader);

        var loadedReader = loadMessageReader(language);
        if (loadedReader == null) return Optional.empty();

        _languageMessagesMap.put(language, loadedReader);

        return Optional.of(loadedReader);
    }

    @SneakyThrows
    private ConfigReader loadMessageReader(String language) {
        var languageDirectory = _baseDirectory.resolve(language).toFile();
        if (!languageDirectory.exists() || !languageDirectory.isDirectory()) {
            ServerSystem.getLog().warning("Requested language '${language}', but doesn't exist!");
            return null;
        }
        var messageFile = languageDirectory.toPath().resolve("messages.yml").toFile();
        if (!messageFile.exists()) {
            ServerSystem.getLog().warning("Requested message language '${language}', but doesn't exist!");
            return null;
        }

        return DefaultConfigReader.loadConfiguration(messageFile);
    }


    public Optional<ConfigReader> getMappingReader(String language) {
        language = language.toLowerCase();
        var configReader = _languageMappingsMap.get(language);
        if (configReader != null) return Optional.of(configReader);

        var loadedReader = loadMappingReader(language);
        if (loadedReader == null) return Optional.empty();

        _languageMappingsMap.put(language, loadedReader);

        return Optional.of(loadedReader);
    }

    @SneakyThrows
    private ConfigReader loadMappingReader(String language) {
        var languageDirectory = _baseDirectory.resolve(language).toFile();
        if (!languageDirectory.exists() || !languageDirectory.isDirectory()) {
            ServerSystem.getLog().warning("Requested language '${language}', but doesn't exist!");
            return null;
        }
        var mappingsFile = languageDirectory.toPath().resolve("mappings.yml").toFile();
        if (!mappingsFile.exists()) {
            ServerSystem.getLog().warning("Requested mapping language '${language}', but doesn't exist!");
            return null;
        }

        return DefaultConfigReader.loadConfiguration(mappingsFile);
    }
}