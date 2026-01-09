package me.testaccount666.serversystem.utils

import org.bukkit.ChatColor
import java.util.logging.Level
import java.util.logging.Logger
import java.util.regex.Pattern

/**
 * Represents the various chat colors and formatting options available in Minecraft.
 * This class provides methods to apply colors and formatting to chat messages.
 */
enum class ChatColor {
    BLACK,
    DARK_BLUE,
    DARK_GREEN,
    DARK_AQUA,
    DARK_RED,
    DARK_PURPLE,
    GOLD,
    GRAY,
    DARK_GRAY,
    BLUE,
    GREEN,
    AQUA,
    RED,
    LIGHT_PURPLE,
    YELLOW,
    WHITE,
    MAGIC,
    BOLD,
    STRIKETHROUGH,
    UNDERLINE,
    ITALIC,
    RESET;

    /**
     * The underlying Bukkit ChatColor.
     */
    @JvmField
    val _chatColor: ChatColor = ChatColor.valueOf(name)

    /**
     * Returns the string representation of this color.
     * 
     * @return The color code as a string
     */
    override fun toString(): String = _chatColor.toString()

    companion object {
        /**
         * The character used to indicate a color code in Minecraft chat.
         */
        const val COLOR_CHAR: Char = '§'

        /**
         * Pattern to match hex color codes.
         */
        @JvmField
        val HEX_PATTERN: Pattern = Pattern.compile("(&)?&#([0-9a-fA-F]{6})")

        /**
         * Pattern to strip color codes from text.
         */
        private val _STRIP_COLOR_PATTERN: Pattern = Pattern.compile("(?i)$COLOR_CHAR[0-9A-FK-OR]")

        /**
         * Logger for error reporting.
         */
        private val _LOGGER: Logger = Logger.getLogger(me.testaccount666.serversystem.utils.ChatColor::class.java.name)

        /**
         * Translates alternate color codes in a string to the internal Minecraft color code format.
         * Supports both standard color codes and hex color codes.
         * 
         * @param text The text to translate
         * @return The translated text with proper Minecraft color codes
         */
        @JvmStatic
        fun translateColor(text: String): String = translateAlternateColorCodes('&', text)

        /**
         * Translates alternate color codes in a string to the internal Minecraft color code format.
         * Supports both standard color codes and hex color codes.
         * 
         * @param altColorChar The alternate color code character to replace (e.g., '&')
         * @param text         The text to translate
         * @return The translated text with proper Minecraft color codes
         */
        @JvmStatic
        fun translateAlternateColorCodes(altColorChar: Char, text: String?): String {
            var text = text
            if (text.isNullOrEmpty()) return ""

            // Process hex colors first
            if (text.contains("$altColorChar#")) try {
                text = replaceHexColor(altColorChar, text)
            } catch (exception: Exception) {
                _LOGGER.log(Level.WARNING, "Error processing hex colors", exception)
            }

            // Then process standard color codes
            return ChatColor.translateAlternateColorCodes(altColorChar, text)
        }

        /**
         * Replaces hex color codes in a string with the internal Minecraft color code format.
         * 
         * @param altColorChar The alternate color code character to replace (e.g., '&')
         * @param input        The text containing hex color codes
         * @return The text with hex color codes replaced
         */
        @JvmStatic
        fun replaceHexColor(altColorChar: Char, input: String?): String {
            if (input.isNullOrEmpty()) return ""

            val result = StringBuilder()
            val matcher = Pattern.compile(HEX_PATTERN.pattern().replace("&", altColorChar.toString())).matcher(input)

            while (matcher.find()) matcher.appendReplacement(result, convertHexToMinecraft(matcher.group(2)))
            matcher.appendTail(result)

            return result.toString()
        }

        /**
         * Converts a hex color code to the internal Minecraft color code format.
         * 
         * @param hexCode The hex color code (without the # prefix)
         * @return The converted Minecraft color code
         */
        @JvmStatic
        fun convertHexToMinecraft(hexCode: String?): String {
            if (hexCode?.length != 6) return ""

            val result = StringBuilder(14)
            result.append(COLOR_CHAR).append('x')

            for (c in hexCode.toCharArray()) result.append(COLOR_CHAR).append(c.lowercaseChar())

            return result.toString()
        }

        /**
         * Strips all color codes from a string.
         * 
         * @param input The text to strip color codes from
         * @return The text without color codes
         */
        @JvmStatic
        fun stripColor(input: String?): String {
            if (input.isNullOrEmpty()) return ""
            return _STRIP_COLOR_PATTERN.matcher(input).replaceAll("")
        }
    }
}
