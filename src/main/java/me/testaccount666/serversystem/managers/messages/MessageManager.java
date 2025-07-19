package me.testaccount666.serversystem.managers.messages;

import lombok.Getter;
import lombok.SneakyThrows;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.PlaceholderManager;
import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import me.testaccount666.serversystem.utils.ComponentColor;
import net.kyori.adventure.text.Component;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.logging.Level;

public class MessageManager {
    @Getter
    private static LanguageLoader _LanguageLoader;
    private static PlaceholderManager _PlaceholderManager;

    private MessageManager() {
    }

    public static void initialize() throws FileNotFoundException {
        _PlaceholderManager = new PlaceholderManager();
        _LanguageLoader = new LanguageLoader();
    }

    public static String formatMessage(String message, User commandSender, @Nullable String targetName, @Nullable String label, boolean addPrefix) {
        if (message == null) return "";

        if (addPrefix) {
            var prefix = getMessage(commandSender, "General.Prefix").orElse("");
            message = prefix + message;
        }

        return ChatColor.translateColor(applyPlaceholders(message, commandSender, targetName, label));
    }

    public static String applyPlaceholders(String message, User commandSender, @Nullable String targetName, @Nullable String label) {
        return _PlaceholderManager.applyPlaceholders(message, commandSender, targetName, label != null? label : "");
    }

    /**
     * Formats a message with placeholders and color codes, returning a Component.
     * This method is similar to formatMessage but returns a Component instead of a String.
     *
     * @param message       The message to format
     * @param commandSender The user who sent the command
     * @param targetName    The name of the target user (optional)
     * @param label         The command label (optional)
     * @param addPrefix     Whether to add the prefix to the message
     * @return The formatted message as a Component
     */
    public static Component formatMessageAsComponent(String message, User commandSender, @Nullable String targetName, @Nullable String label, boolean addPrefix) {
        if (message == null) return Component.empty();

        if (addPrefix) {
            var prefix = getMessage(commandSender, "General.Prefix").orElse("");
            message = prefix + message;
        }

        var processedMessage = _PlaceholderManager.applyPlaceholders(message, commandSender, targetName, label != null? label : "");
        return ComponentColor.translateToComponent(processedMessage);
    }

    /**
     * Gets a message from the messages file for the specified user's language.
     *
     * @param user        The user to get the message for
     * @param messagePath The path to the message in the messages file
     * @return An Optional containing the message, or empty if not found
     */
    @SneakyThrows
    public static Optional<String> getMessage(User user, String messagePath) {
        if (_PlaceholderManager == null) throw new IllegalStateException("MessageManager was not yet initialized. Call initialize first.");
        messagePath = "Messages.${messagePath}";

        var language = user != null? user.getPlayerLanguage() : "english";


        ConfigReader reader;
        try {
            reader = getConfigReader(language);
        } catch (FileNotFoundException exception) {
            ServerSystem.getLog().log(Level.WARNING, "Failed to load messages for language '${language}': ${exception.getMessage()}", exception);
            reader = getConfigReader("english");
        }

        var message = reader.getString(messagePath, null);

        if (message == null) {
            ServerSystem.getLog().warning("Message '${messagePath}' not found for language ${language}!");
            return Optional.empty();
        }

        return Optional.of(message);
    }

    /**
     * Gets the ConfigReader for the specified language.
     *
     * @param language The language to get the ConfigReader for
     * @return The ConfigReader for the specified language
     * @throws FileNotFoundException If the messages file couldn't be found
     */
    private static ConfigReader getConfigReader(String language) throws FileNotFoundException {
        var readerOptional = _LanguageLoader.getMessageReader(language);
        //noinspection OptionalGetWithoutIsPresent
        return readerOptional.orElseGet(() -> _LanguageLoader.getMessageReader("english").get());
    }
}
