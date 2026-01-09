package me.testaccount666.serversystem.managers.messages

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.managers.PlaceholderManager
import me.testaccount666.serversystem.managers.config.ConfigReader
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ChatColor
import me.testaccount666.serversystem.utils.ComponentColor
import net.kyori.adventure.text.Component
import java.io.FileNotFoundException
import java.util.*
import java.util.function.Supplier
import java.util.logging.Level

object MessageManager {
    const val FALLBACK_LANGUAGE: String = "english"

    @JvmStatic
    lateinit var languageLoader: LanguageLoader

    @JvmStatic
    lateinit var placeholderManager: PlaceholderManager

    @JvmStatic
    var defaultLanguage: String? = null

    @JvmStatic
    fun initialize() {
        placeholderManager = PlaceholderManager()
        languageLoader = LanguageLoader()
        defaultLanguage = ServerSystem.instance.registry.getService<ConfigurationManager>().generalConfig!!
            .getString("Language.DefaultLanguage", FALLBACK_LANGUAGE)

        val messageOptional = languageLoader.getMessageReader(defaultLanguage!!)
        val mappingsOptional = languageLoader.getMappingReader(defaultLanguage!!)

        if (messageOptional.isPresent && mappingsOptional.isPresent) return
        ServerSystem.log.warning("Could not load default language '${defaultLanguage}'! Falling back to '${FALLBACK_LANGUAGE}'...")
        ServerSystem.log.warning("Make sure you have a 'messages.yml' AND 'mappings.yml' file in your language folder!")
        defaultLanguage = FALLBACK_LANGUAGE
    }

    @JvmStatic
    fun formatMessage(message: String?, commandSender: User, targetName: String?, label: String?, addPrefix: Boolean): String {
        var message = message ?: return ""

        if (addPrefix) {
            val prefix = getMessage(commandSender, "General.Prefix").orElse("")
            message = prefix + message
        }

        return ChatColor.translateColor(applyPlaceholders(message, commandSender, targetName, label))
    }

    @JvmStatic
    fun applyPlaceholders(message: String, commandSender: User, targetName: String?, label: String?): String {
        return placeholderManager.applyPlaceholders(message, commandSender, targetName, label ?: "")
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
    @JvmStatic
    fun formatMessageAsComponent(message: String?, commandSender: User, targetName: String?, label: String?, addPrefix: Boolean): Component {
        var message = message ?: return Component.empty()

        if (addPrefix) {
            val prefix = getMessage(commandSender, "General.Prefix").orElse("")
            message = prefix + message
        }

        val processedMessage = placeholderManager.applyPlaceholders(message, commandSender, targetName, label ?: "")
        return ComponentColor.translateToComponent(processedMessage)
    }

    /**
     * Gets a message from the messages file for the specified user's language.
     *
     * @param user        The user to get the message for
     * @param messagePath The path to the message in the messages file
     * @return An Optional containing the message, or empty if not found
     */
    @JvmStatic
    fun getMessage(user: User, messagePath: String): Optional<String> {
        val language = user.playerLanguage

        return getMessage(user, messagePath, language)
    }

    /**
     * Gets a message from the messages file for the specified user's language.
     *
     * @param user        The user to get the message for
     * @param messagePath The path to the message in the messages file
     * @return An Optional containing the message, or empty if not found
     */
    fun getMessage(user: User, messagePath: String, language: String): Optional<String> {
        var messagePath = messagePath
        messagePath = "Messages.${messagePath}"


        var reader: ConfigReader
        try {
            reader = getConfigReader(language)
        } catch (exception: FileNotFoundException) {
            ServerSystem.log.log(Level.WARNING, "Failed to load messages for language '${language}': ${exception.message}", exception)
            reader = getConfigReader(defaultLanguage!!)
        }

        val message = reader.getString(messagePath, null)

        if (message == null) {
            ServerSystem.log.warning("Message '${messagePath}' not found for language ${language}!")
            return Optional.empty<String?>()
        }

        return Optional.of<String?>(message)
    }

    /**
     * Gets the ConfigReader for the specified language.
     *
     * @param language The language to get the ConfigReader for
     * @return The ConfigReader for the specified language
     * @throws FileNotFoundException If the messages file couldn't be found
     */
    private fun getConfigReader(language: String): ConfigReader {
        val readerOptional = languageLoader.getMessageReader(language)
        return readerOptional.orElseGet(Supplier { languageLoader.getMessageReader(defaultLanguage!!).get() })
    }
}