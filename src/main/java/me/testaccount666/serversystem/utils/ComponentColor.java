package me.testaccount666.serversystem.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for creating and formatting text components using the Component API.
 * This class provides similar functionality to ChatColor but returns Component objects
 * instead of formatted strings.
 */
public final class ComponentColor {

    /**
     * Logger for error reporting.
     */
    private static final Logger _LOGGER = Logger.getLogger(ComponentColor.class.getName());

    /**
     * Map of legacy color codes to NamedTextColor
     */
    private static final Map<Character, NamedTextColor> _COLOR_MAP = new HashMap<>();

    /**
     * Map of legacy formatting codes to TextDecoration
     */
    private static final Map<Character, TextDecoration> _DECORATION_MAP = new HashMap<>();

    /**
     * Reverse map of NamedTextColor to legacy color codes
     */
    private static final Map<NamedTextColor, Character> _REVERSE_COLOR_MAP = new HashMap<>();

    /**
     * Reverse map of TextDecoration to legacy formatting codes
     */
    private static final Map<TextDecoration, Character> _REVERSE_DECORATION_MAP = new HashMap<>();

    static {
        _COLOR_MAP.put('0', NamedTextColor.BLACK);
        _COLOR_MAP.put('1', NamedTextColor.DARK_BLUE);
        _COLOR_MAP.put('2', NamedTextColor.DARK_GREEN);
        _COLOR_MAP.put('3', NamedTextColor.DARK_AQUA);
        _COLOR_MAP.put('4', NamedTextColor.DARK_RED);
        _COLOR_MAP.put('5', NamedTextColor.DARK_PURPLE);
        _COLOR_MAP.put('6', NamedTextColor.GOLD);
        _COLOR_MAP.put('7', NamedTextColor.GRAY);
        _COLOR_MAP.put('8', NamedTextColor.DARK_GRAY);
        _COLOR_MAP.put('9', NamedTextColor.BLUE);
        _COLOR_MAP.put('a', NamedTextColor.GREEN);
        _COLOR_MAP.put('b', NamedTextColor.AQUA);
        _COLOR_MAP.put('c', NamedTextColor.RED);
        _COLOR_MAP.put('d', NamedTextColor.LIGHT_PURPLE);
        _COLOR_MAP.put('e', NamedTextColor.YELLOW);
        _COLOR_MAP.put('f', NamedTextColor.WHITE);

        _DECORATION_MAP.put('k', TextDecoration.OBFUSCATED);
        _DECORATION_MAP.put('l', TextDecoration.BOLD);
        _DECORATION_MAP.put('m', TextDecoration.STRIKETHROUGH);
        _DECORATION_MAP.put('n', TextDecoration.UNDERLINED);
        _DECORATION_MAP.put('o', TextDecoration.ITALIC);

        // Populate reverse maps
        _COLOR_MAP.forEach((key, value) -> _REVERSE_COLOR_MAP.put(value, key));
        _DECORATION_MAP.forEach((key, value) -> _REVERSE_DECORATION_MAP.put(value, key));
    }

    private ComponentColor() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Translates a string with color codes to a Component.
     * Supports both standard color codes and hex color codes.
     *
     * @param text The text to translate
     * @return The translated text as a Component
     */
    public static Component translateToComponent(String text) {
        return translateAlternateColorCodesToComponent('&', text);
    }

    /**
     * Translates a string with alternate color codes to a Component.
     * Supports both standard color codes and hex color codes.
     *
     * @param altColorChar The alternate color code character to replace (e.g., '&')
     * @param text         The text to translate
     * @return The translated text as a Component
     */
    public static Component translateAlternateColorCodesToComponent(char altColorChar, String text) {
        if (text == null || text.isEmpty()) return Component.empty();

        return parseColoredText(altColorChar, text);
    }

    /**
     * Creates a Component with the specified color from a hex code.
     *
     * @param text    The text content
     * @param hexCode The hex color code (e.g., "#FF0000" for red)
     * @return The colored Component
     */
    public static Component coloredHex(String text, String hexCode) {
        if (text == null) text = "";
        if (hexCode == null) hexCode = "";
        if (hexCode.isEmpty()) return Component.empty();

        if (!hexCode.startsWith("#")) hexCode = "#" + hexCode;
        if (hexCode.length() < 7) return Component.text(text);

        try {
            var color = TextColor.fromHexString(hexCode);
            return Component.text(text).color(color);
        } catch (IllegalArgumentException exception) {
            _LOGGER.log(Level.WARNING, "Invalid hex color code: ${hexCode}", exception);
            return Component.text(text);
        }
    }

    /**
     * Parses text with color codes and creates Components directly.
     * This method handles both standard color codes and hex color codes.
     *
     * @param altColorChar The alternate color code character to replace (e.g., '&')
     * @param text         The text to parse
     * @return A Component representing the parsed text with colors and decorations
     */
    private static Component parseColoredText(char altColorChar, String text) {
        if (text == null || text.isEmpty()) return Component.empty();
        if (!text.contains(String.valueOf(altColorChar))) return Component.text(text);

        var textParts = text.split(String.valueOf(altColorChar));
        var componentBuilder = Component.text();

        // Handle the first part (before any color codes)
        if (textParts.length > 0) componentBuilder.append(Component.text(textParts[0]));

        // If there are no color codes after the split, return the text as is
        if (textParts.length <= 1) return componentBuilder.build();

        var formatState = new FormatState();

        for (var index = 1; index < textParts.length; index++) {
            var partComponent = processTextPart(altColorChar, textParts[index], formatState);
            componentBuilder.append(partComponent);
        }

        return componentBuilder.build();
    }

    /**
     * Processes a single part of text that may start with a formatting code.
     *
     * @param altColorChar The alternate color code character
     * @param textPart     The part of text to process (may start with a format code)
     * @param formatState  The current formatting state
     * @return A component representing the processed text part
     */
    private static Component processTextPart(char altColorChar, String textPart, FormatState formatState) {
        if (textPart.isEmpty()) return Component.text(String.valueOf(altColorChar));

        var formatCode = textPart.charAt(0);
        if (formatCode == '#' && textPart.length() >= 7) return processHexColorCode(altColorChar, textPart, formatState);

        formatCode = Character.toLowerCase(formatCode);
        if (_COLOR_MAP.containsKey(formatCode)) return processStandardColorCode(formatCode, textPart, formatState);

        if (_DECORATION_MAP.containsKey(formatCode)) return processDecorationCode(formatCode, textPart, formatState);

        if (formatCode == 'r') return processResetCode(textPart, formatState);

        return Component.text(altColorChar + textPart);
    }

    /**
     * Processes a hex color code in the text.
     *
     * @param altColorChar The alternate color code character
     * @param textPart     The part of text starting with a hex color code
     * @param formatState  The current formatting state
     * @return A component with the hex color applied
     */
    private static Component processHexColorCode(char altColorChar, String textPart, FormatState formatState) {
        var hexCode = textPart.substring(0, 7);
        var remainingText = textPart.substring(7);

        try {
            formatState.currentColor = TextColor.fromHexString(hexCode);
            formatState.activeDecorations.clear();
            return coloredHex(remainingText, hexCode);
        } catch (IllegalArgumentException ignored) {
            return Component.text(altColorChar + textPart);
        }
    }

    /**
     * Processes a standard color code in the text.
     *
     * @param colorCode   The color code character
     * @param textPart    The part of text starting with a color code
     * @param formatState The current formatting state
     * @return A component with the color applied
     */
    private static Component processStandardColorCode(char colorCode, String textPart, FormatState formatState) {
        formatState.currentColor = _COLOR_MAP.get(colorCode);
        formatState.activeDecorations.clear();
        return Component.text(textPart.substring(1)).color(formatState.currentColor);
    }

    /**
     * Processes a decoration code in the text.
     *
     * @param decorationCode The decoration code character
     * @param textPart       The part of text starting with a decoration code
     * @param formatState    The current formatting state
     * @return A component with the decoration applied
     */
    private static Component processDecorationCode(char decorationCode, String textPart, FormatState formatState) {
        var decoration = _DECORATION_MAP.get(decorationCode);
        formatState.activeDecorations.add(decoration);

        var component = Component.text(textPart.substring(1)).decoration(decoration, true);

        for (var activeDecoration : formatState.activeDecorations) component = component.decoration(activeDecoration, true);

        if (formatState.currentColor != null) component = component.color(formatState.currentColor);

        return component;
    }

    /**
     * Processes a reset code in the text.
     *
     * @param textPart    The part of text starting with a reset code
     * @param formatState The current formatting state
     * @return A component with all formatting reset
     */
    private static Component processResetCode(String textPart, FormatState formatState) {
        var component = Component.text(textPart.substring(1));

        for (var decoration : _DECORATION_MAP.values()) component = component.decoration(decoration, false);
        component = component.color(NamedTextColor.WHITE);

        formatState.resetFormatting();

        return component;
    }

    /**
     * Converts a Component to a string with color codes.
     * This is the reverse operation of translateToComponent.
     *
     * @param component The Component to convert
     * @return The string with color codes using '&' as the alternate color character
     */
    public static String componentToString(Component component) {
        return componentToString(component, '&');
    }

    /**
     * Converts a Component to a string with the specified alternate color codes.
     * This is the reverse operation of translateAlternateColorCodesToComponent.
     *
     * @param component    The Component to convert
     * @param altColorChar The alternate color code character to use (e.g., '&')
     * @return The string with color codes
     */
    public static String componentToString(Component component, char altColorChar) {
        if (component == null) return "";

        var result = new StringBuilder();
        appendComponentToString(component, result, altColorChar);
        return result.toString();
    }

    /**
     * Recursively appends a Component and its children to a StringBuilder with color codes.
     *
     * @param component    The Component to convert
     * @param builder      The StringBuilder to append to
     * @param altColorChar The alternate color code character to use
     */
    private static void appendComponentToString(Component component, StringBuilder builder, char altColorChar) {
        if (!(component instanceof TextComponent textComponent)) {
            for (var child : component.children()) appendComponentToString(child, builder, altColorChar);
            return;
        }

        var color = component.color();
        if (color != null) if (color instanceof NamedTextColor namedColor) {
            var colorCode = _REVERSE_COLOR_MAP.get(namedColor);
            if (colorCode != null) builder.append(altColorChar).append(colorCode);
            else {
                var hexString = color.asHexString().toUpperCase();
                builder.append(altColorChar).append(hexString);
            }
        } else {
            var hexString = color.asHexString().toUpperCase();
            builder.append(altColorChar).append(hexString);
        }

        for (var decoration : TextDecoration.values())
            if (component.decoration(decoration) == TextDecoration.State.TRUE) {
                var decorationCode = _REVERSE_DECORATION_MAP.get(decoration);
                if (decorationCode != null) builder.append(altColorChar).append(decorationCode);
            }

        builder.append(textComponent.content());

        for (var child : component.children()) appendComponentToString(child, builder, altColorChar);
    }

    /**
     * Represents the current formatting state while processing colored text.
     * Tracks the current color and active decorations.
     */
    private static class FormatState {
        TextColor currentColor = null;
        ArrayList<TextDecoration> activeDecorations = new ArrayList<>();

        void resetFormatting() {
            currentColor = null;
            activeDecorations.clear();
        }
    }
}