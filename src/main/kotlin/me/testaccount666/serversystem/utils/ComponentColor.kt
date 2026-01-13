package me.testaccount666.serversystem.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Utility class for creating and formatting text components using the Component API.
 * This class provides similar functionality to ChatColor but returns Component objects
 * instead of formatted strings.
 */
class ComponentColor private constructor() {
    init {
        throw UnsupportedOperationException("This class cannot be instantiated")
    }

    /**
     * Represents the current formatting state while processing colored text.
     * Tracks the current color and active decorations.
     */
    private class FormatState {
        var currentColor: TextColor? = null
        var activeDecorations = ArrayList<TextDecoration>()

        fun resetFormatting() {
            currentColor = null
            activeDecorations.clear()
        }
    }

    companion object {
        /**
         * Logger for error reporting.
         */
        private val _LOGGER = Logger.getLogger(ComponentColor::class.java.name)

        /**
         * Map of legacy color codes to NamedTextColor
         */
        private val _COLOR_MAP = HashMap<Char, NamedTextColor>()

        /**
         * Map of legacy formatting codes to TextDecoration
         */
        private val _DECORATION_MAP = HashMap<Char, TextDecoration>()

        /**
         * Reverse map of NamedTextColor to legacy color codes
         */
        private val _REVERSE_COLOR_MAP = HashMap<NamedTextColor, Char>()

        /**
         * Reverse map of TextDecoration to legacy formatting codes
         */
        private val _REVERSE_DECORATION_MAP = HashMap<TextDecoration, Char>()

        init {
            _COLOR_MAP['0'] = NamedTextColor.BLACK
            _COLOR_MAP['1'] = NamedTextColor.DARK_BLUE
            _COLOR_MAP['2'] = NamedTextColor.DARK_GREEN
            _COLOR_MAP['3'] = NamedTextColor.DARK_AQUA
            _COLOR_MAP['4'] = NamedTextColor.DARK_RED
            _COLOR_MAP['5'] = NamedTextColor.DARK_PURPLE
            _COLOR_MAP['6'] = NamedTextColor.GOLD
            _COLOR_MAP['7'] = NamedTextColor.GRAY
            _COLOR_MAP['8'] = NamedTextColor.DARK_GRAY
            _COLOR_MAP['9'] = NamedTextColor.BLUE
            _COLOR_MAP['a'] = NamedTextColor.GREEN
            _COLOR_MAP['b'] = NamedTextColor.AQUA
            _COLOR_MAP['c'] = NamedTextColor.RED
            _COLOR_MAP['d'] = NamedTextColor.LIGHT_PURPLE
            _COLOR_MAP['e'] = NamedTextColor.YELLOW
            _COLOR_MAP['f'] = NamedTextColor.WHITE

            _DECORATION_MAP['k'] = TextDecoration.OBFUSCATED
            _DECORATION_MAP['l'] = TextDecoration.BOLD
            _DECORATION_MAP['m'] = TextDecoration.STRIKETHROUGH
            _DECORATION_MAP['n'] = TextDecoration.UNDERLINED
            _DECORATION_MAP['o'] = TextDecoration.ITALIC

            // Populate reverse maps
            _COLOR_MAP.forEach { (key, value) -> _REVERSE_COLOR_MAP[value] = key }
            _DECORATION_MAP.forEach { (key, value) -> _REVERSE_DECORATION_MAP[value] = key }
        }

        /**
         * Translates a string with color codes to a Component.
         * Supports both standard color codes and hex color codes.
         * 
         * @param text The text to translate
         * @return The translated text as a Component
         */
        @JvmStatic
        fun translateToComponent(text: String?) = translateAlternateColorCodesToComponent('&', text)

        /**
         * Translates a string with alternate color codes to a Component.
         * Supports both standard color codes and hex color codes.
         * 
         * @param altColorChar The alternate color code character to replace (e.g., '&')
         * @param text         The text to translate
         * @return The translated text as a Component
         */
        fun translateAlternateColorCodesToComponent(altColorChar: Char, text: String?): Component {
            if (text.isNullOrEmpty()) return Component.empty()

            return parseColoredText(altColorChar, text)
        }

        /**
         * Creates a Component with the specified color from a hex code.
         * 
         * @param text    The text content
         * @param hexCode The hex color code (e.g., "#FF0000" for red)
         * @return The colored Component
         */
        fun coloredHex(text: String?, hexCode: String?): Component {
            val text = text ?: ""
            var hexCode = hexCode ?: ""

            if (hexCode.isEmpty()) return Component.empty()

            if (!hexCode.startsWith("#")) hexCode = "#$hexCode"
            if (hexCode.length < 7) return Component.text(text)

            try {
                val color = TextColor.fromHexString(hexCode)
                return Component.text(text).color(color)
            } catch (exception: IllegalArgumentException) {
                _LOGGER.log(Level.WARNING, "Invalid hex color code: $hexCode", exception)
                return Component.text(text)
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
        private fun parseColoredText(altColorChar: Char, text: String?): Component {
            if (text.isNullOrEmpty()) return Component.empty()
            if (!text.contains(altColorChar.toString())) return Component.text(text)

            val textParts = text.split(altColorChar.toString().toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val componentBuilder = Component.text()

            // Handle the first part (before any color codes)
            if (textParts.isNotEmpty()) componentBuilder.append(Component.text(textParts[0]))

            // If there are no color codes after the split, return the text as is
            if (textParts.size <= 1) return componentBuilder.build()

            val formatState = FormatState()

            for (index in 1..<textParts.size) {
                val partComponent = processTextPart(altColorChar, textParts[index], formatState)
                componentBuilder.append(partComponent)
            }

            return componentBuilder.build()
        }

        /**
         * Processes a single part of text that may start with a formatting code.
         * 
         * @param altColorChar The alternate color code character
         * @param textPart     The part of text to process (may start with a format code)
         * @param formatState  The current formatting state
         * @return A component representing the processed text part
         */
        private fun processTextPart(altColorChar: Char, textPart: String, formatState: FormatState): Component {
            if (textPart.isEmpty()) return Component.text(altColorChar.toString())

            var formatCode = textPart[0]
            if (formatCode == '#' && textPart.length >= 7) return processHexColorCode(altColorChar, textPart, formatState)

            formatCode = formatCode.lowercaseChar()
            if (_COLOR_MAP.containsKey(formatCode)) return processStandardColorCode(formatCode, textPart, formatState)

            if (_DECORATION_MAP.containsKey(formatCode)) return processDecorationCode(formatCode, textPart, formatState)

            if (formatCode == 'r') return processResetCode(textPart, formatState)

            return Component.text(altColorChar.toString() + textPart)
        }

        /**
         * Processes a hex color code in the text.
         * 
         * @param altColorChar The alternate color code character
         * @param textPart     The part of text starting with a hex color code
         * @param formatState  The current formatting state
         * @return A component with the hex color applied
         */
        private fun processHexColorCode(altColorChar: Char, textPart: String, formatState: FormatState): Component {
            val hexCode = textPart.substring(0, 7)
            val remainingText = textPart.substring(7)

            try {
                formatState.currentColor = TextColor.fromHexString(hexCode)
                formatState.activeDecorations.clear()
                return coloredHex(remainingText, hexCode)
            } catch (_: IllegalArgumentException) {
                return Component.text(altColorChar.toString() + textPart)
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
        private fun processStandardColorCode(colorCode: Char, textPart: String, formatState: FormatState): Component {
            formatState.currentColor = _COLOR_MAP[colorCode]
            formatState.activeDecorations.clear()
            return Component.text(textPart.substring(1)).color(formatState.currentColor)
        }

        /**
         * Processes a decoration code in the text.
         * 
         * @param decorationCode The decoration code character
         * @param textPart       The part of text starting with a decoration code
         * @param formatState    The current formatting state
         * @return A component with the decoration applied
         */
        private fun processDecorationCode(decorationCode: Char, textPart: String, formatState: FormatState): Component {
            val decoration = _DECORATION_MAP[decorationCode]!!
            formatState.activeDecorations.add(decoration)

            var component = Component.text(textPart.substring(1)).decoration(decoration, true)

            for (activeDecoration in formatState.activeDecorations) component = component.decoration(activeDecoration, true)

            if (formatState.currentColor != null) component = component.color(formatState.currentColor)

            return component
        }

        /**
         * Processes a reset code in the text.
         * 
         * @param textPart    The part of text starting with a reset code
         * @param formatState The current formatting state
         * @return A component with all formatting reset
         */
        private fun processResetCode(textPart: String, formatState: FormatState): Component {
            var component = Component.text(textPart.substring(1))

            _DECORATION_MAP.values.forEach { component = component.decoration(it, false) }
            component = component.color(NamedTextColor.WHITE)

            formatState.resetFormatting()
            return component
        }

        /**
         * Converts a Component to a string with color codes.
         * This is the reverse operation of translateToComponent.
         * 
         * @param component The Component to convert
         * @return The string with color codes using '&' as the alternate color character
         */
        @JvmOverloads
        fun componentToString(component: Component?, altColorChar: Char = '&'): String {
            if (component == null) return ""

            val result = StringBuilder()
            appendComponentToString(component, result, altColorChar)
            return result.toString()
        }

        /**
         * Recursively appends a Component and its children to a StringBuilder with color codes.
         * 
         * @param component    The Component to convert
         * @param builder      The StringBuilder to append to
         * @param altColorChar The alternate color code character to use
         */
        private fun appendComponentToString(component: Component, builder: StringBuilder, altColorChar: Char) {
            if (component !is TextComponent) {
                for (child in component.children()) appendComponentToString(child, builder, altColorChar)
                return
            }

            val color = component.color()
            if (color != null) if (color is NamedTextColor) {
                val colorCode = _REVERSE_COLOR_MAP[color]
                if (colorCode != null) builder.append(altColorChar).append(colorCode)
                else {
                    val hexString = color.asHexString().uppercase(Locale.getDefault())
                    builder.append(altColorChar).append(hexString)
                }
            } else {
                val hexString = color.asHexString().uppercase(Locale.getDefault())
                builder.append(altColorChar).append(hexString)
            }

            for (decoration in TextDecoration.entries) if (component.decoration(decoration) == TextDecoration.State.TRUE) {
                val decorationCode = _REVERSE_DECORATION_MAP[decoration]
                if (decorationCode != null) builder.append(altColorChar).append(decorationCode)
            }

            builder.append(component.content())

            for (child in component.children()) appendComponentToString(child, builder, altColorChar)
        }
    }
}