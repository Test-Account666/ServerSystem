package me.testaccount666.serversystem.utils;

import lombok.Getter;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Represents the various chat colors and formatting options available in Minecraft.
 * This class provides methods to apply colors and formatting to chat messages.
 */
@Getter
public enum ChatColor {
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
     * The character used to indicate a color code in Minecraft chat.
     */
    public static final char COLOR_CHAR = '§';

    /**
     * Pattern to match hex color codes.
     */
    public static final Pattern HEX_PATTERN = Pattern.compile("(&)?&#([0-9a-fA-F]{6})");

    /**
     * Pattern to strip color codes from text.
     */
    private static final Pattern _STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");

    /**
     * Logger for error reporting.
     */
    private static final Logger _LOGGER = Logger.getLogger(ChatColor.class.getName());

    /**
     * The underlying Bukkit ChatColor.
     */
    @Getter
    private final org.bukkit.ChatColor _chatColor;

    /**
     * Constructor that maps this enum to the corresponding Bukkit ChatColor.
     */
    ChatColor() {
        _chatColor = org.bukkit.ChatColor.valueOf(name());
    }

    /**
     * Translates alternate color codes in a string to the internal Minecraft color code format.
     * Supports both standard color codes and hex color codes.
     *
     * @param text The text to translate
     * @return The translated text with proper Minecraft color codes
     */
    public static String translateColorCodes(String text) {
        return translateAlternateColorCodes('&', text);
    }

    /**
     * Translates alternate color codes in a string to the internal Minecraft color code format.
     * Supports both standard color codes and hex color codes.
     *
     * @param altColorChar The alternate color code character to replace (e.g., '&')
     * @param text         The text to translate
     * @return The translated text with proper Minecraft color codes
     */
    public static String translateAlternateColorCodes(char altColorChar, String text) {
        if (text == null || text.isEmpty()) return "";

        // Process hex colors first
        if (text.contains(altColorChar + "#")) try {
            text = replaceHexColor(altColorChar, text);
        } catch (Exception exception) {
            _LOGGER.log(Level.WARNING, "Error processing hex colors", exception);
        }

        // Then process standard color codes
        return org.bukkit.ChatColor.translateAlternateColorCodes(altColorChar, text);
    }

    /**
     * Replaces hex color codes in a string with the internal Minecraft color code format.
     *
     * @param altColorChar The alternate color code character to replace (e.g., '&')
     * @param input        The text containing hex color codes
     * @return The text with hex color codes replaced
     */
    public static String replaceHexColor(char altColorChar, String input) {
        if (input == null || input.isEmpty()) return "";

        var result = new StringBuilder();
        var matcher = Pattern.compile(HEX_PATTERN.pattern().replace("&", String.valueOf(altColorChar))).matcher(input);

        while (matcher.find()) matcher.appendReplacement(result, convertHexToMinecraft(matcher.group(2)));
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Converts a hex color code to the internal Minecraft color code format.
     *
     * @param hexCode The hex color code (without the # prefix)
     * @return The converted Minecraft color code
     */
    public static String convertHexToMinecraft(String hexCode) {
        if (hexCode == null || hexCode.length() != 6) return "";

        var result = new StringBuilder(14);
        result.append(COLOR_CHAR).append('x');

        for (var c : hexCode.toCharArray()) result.append(COLOR_CHAR).append(Character.toLowerCase(c));

        return result.toString();
    }

    /**
     * Strips all color codes from a string.
     *
     * @param input The text to strip color codes from
     * @return The text without color codes
     */
    public static String stripColor(String input) {
        if (input == null) return "";
        return _STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    /**
     * Returns the string representation of this color.
     *
     * @return The color code as a string
     */
    @Override
    public String toString() {
        return _chatColor.toString();
    }

}
