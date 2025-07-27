package me.testaccount666.serversystem.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ChatColorTest {

    @ParameterizedTest
    @CsvSource({
        "'&aHello &bWorld', '§a', '§b', '&a', '&b'",
        "'%aHello %bWorld', '§a', '§b', '%a', '%b'"
    })
    void translateColorCodes_shouldTranslateBasicCodes(String input, String expectedColor1, String expectedColor2, String originalCode1, String originalCode2) {
        var result = input.startsWith("%") ? 
            ChatColor.translateAlternateColorCodes('%', input) : 
            ChatColor.translateColor(input);
        
        assertTrue(result.contains(expectedColor1));
        assertTrue(result.contains(expectedColor2));
        assertFalse(result.contains(originalCode1));
        assertFalse(result.contains(originalCode2));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"plain text"})
    void translateColor_shouldHandleNullEmptyAndPlainText(String input) {
        var expected = input == null || input.isEmpty()? "" : input;
        assertEquals(expected, ChatColor.translateColor(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"plain text"})
    void translateAlternateColorCodes_shouldHandleNullEmptyAndPlainText(String input) {
        var expected = input == null || input.isEmpty()? "" : input;
        assertEquals(expected, ChatColor.translateAlternateColorCodes('&', input));
    }


    @Test
    void translateAlternateColorCodes_shouldProcessHexColors() {
        var input = "&#FF0000Red &#00FF00Green";
        var result = ChatColor.translateAlternateColorCodes('&', input);

        assertFalse(result.contains("&#FF0000"));
        assertFalse(result.contains("&#00FF00"));
        assertTrue(result.contains("§x§f§f§0§0§0§0"));
        assertTrue(result.contains("§x§0§0§f§f§0§0"));
        assertTrue(result.contains("Red"));
        assertTrue(result.contains("Green"));
    }

    @Test
    void translateAlternateColorCodes_shouldProcessMixedContent() {
        var input = "Hello &aColored &btext &rwith normal &lformatting and hex &#FF0000colored &#00FF00text";
        var result = ChatColor.translateAlternateColorCodes('&', input);

        assertFalse(result.contains("&a"));
        assertFalse(result.contains("&#00FF00"));
        assertTrue(result.contains("§"));
        assertTrue(result.contains("§x"));
        assertTrue(result.contains("§a"));
    }

    @Test
    void translateAlternateColorCodes_shouldIgnoreInvalidHexColorCodes() {
        var input = "Hello &#GGGGGGcolored &#00FF00text";
        var result = ChatColor.translateAlternateColorCodes('&', input);

        assertTrue(result.contains("&#GGGGGG"));
        assertTrue(result.contains("§x"));
    }

    @Test
    void translateAlternateColorCodes_shouldIgnoreExcessHexColorCodes() {
        var input = "Hello &#FFFFFFFFcolored &#00FF00text";
        var result = ChatColor.translateAlternateColorCodes('&', input);

        assertTrue(result.contains("FFcolored"));
        assertTrue(result.contains("§x"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"plain text"})
    void replaceHexColor_shouldHandleNullEmptyAndPlainText(String input) {
        var expected = input == null || input.isEmpty()? "" : input;
        assertEquals(expected, ChatColor.replaceHexColor('&', input));
    }

    @Test
    void replaceHexColor_shouldReplaceValidHexCodes() {
        var input = "&#FF0000Red text";
        var result = ChatColor.replaceHexColor('&', input);

        assertFalse(result.contains("&#FF0000"));
        assertTrue(result.contains("§x§f§f§0§0§0§0"));
        assertTrue(result.contains("Red text"));
    }

    @Test
    void replaceHexColor_shouldHandleMultipleHexCodes() {
        var input = "&#FF0000Red &#00FF00Green &#0000FFBlue";
        var result = ChatColor.replaceHexColor('&', input);

        assertFalse(result.contains("&#FF0000"));
        assertFalse(result.contains("&#00FF00"));
        assertFalse(result.contains("&#0000FF"));

        assertTrue(result.contains("§x§f§f§0§0§0§0"));
        assertTrue(result.contains("§x§0§0§f§f§0§0"));
        assertTrue(result.contains("§x§0§0§0§0§f§f"));

        assertTrue(result.contains("Red"));
        assertTrue(result.contains("Green"));
        assertTrue(result.contains("Blue"));
    }

    @Test
    void replaceHexColor_shouldHandleCustomColorChar() {
        var input = "%#FF0000Red text";
        var result = ChatColor.replaceHexColor('%', input);

        assertFalse(result.contains("%#FF0000"));
        assertTrue(result.contains("§x§f§f§0§0§0§0"));
        assertTrue(result.contains("Red text"));
    }

    @ParameterizedTest
    @CsvSource({
        ", ''",
        "'', ''",
        "'FF00', ''",
        "'FF0000AA', ''",
        "'GGGGGG', '§x§g§g§g§g§g§g'"
    })
    void convertHexToMinecraft_shouldHandleNullAndInvalid(String input, String expected) {
        assertEquals(expected, ChatColor.convertHexToMinecraft(input));
    }

    @Test
    void convertHexToMinecraft_shouldConvertValidHexCodes() {
        var result = ChatColor.convertHexToMinecraft("FF0000");

        // Should start with §x
        assertTrue(result.startsWith("§x"));
        // Should contain each character of the hex code prefixed with §
        assertTrue(result.contains("§f"));
        assertTrue(result.contains("§0"));
        // Should be the correct length (§x + 6 characters * 2 = 14 characters)
        assertEquals(14, result.length());
    }

    @Test
    void convertHexToMinecraft_shouldHandleLowerAndUpperCase() {
        var upperResult = ChatColor.convertHexToMinecraft("FF0000");
        var lowerResult = ChatColor.convertHexToMinecraft("ff0000");
        var mixedResult = ChatColor.convertHexToMinecraft("Ff0000");

        // All should produce the same result (lowercase)
        assertEquals(upperResult, lowerResult);
        assertEquals(upperResult, mixedResult);

        // Should contain lowercase characters
        assertTrue(upperResult.contains("§f"));
        assertFalse(upperResult.contains("§F"));
    }

    @ParameterizedTest
    @CsvSource({
            ", ''",
            "'', ''",
            "'Plain text without colors', 'Plain text without colors'",
            "'§aHello §bWorld §cTest', 'Hello World Test'",
            "'§lBold §nUnderline §oItalic §rReset', 'Bold Underline Italic Reset'",
            "'§aColored §btext §rwith §lformatting and plain text', 'Colored text with formatting and plain text'"
    })
    void stripColor_shouldHandleVariousInputs(String input, String expected) {
        assertEquals(expected, ChatColor.stripColor(input));
        if (input != null && !input.isEmpty() && expected != null && !expected.isEmpty()) assertFalse(ChatColor.stripColor(input).contains("§"));
    }

    @Test
    void enumValues_shouldHaveCorrectCount() {
        // Verify all expected color values are present
        var values = ChatColor.values();
        assertEquals(22, values.length); // 16 colors + 6 formatting codes (including MAGIC)

        assertNotNull(ChatColor.RED);
        assertNotNull(ChatColor.BLUE);
        assertNotNull(ChatColor.BOLD);
        assertNotNull(ChatColor.RESET);
    }

    @Test
    void toString_shouldReturnBukkitColorString() {
        // This tests the toString method which delegates to Bukkit ChatColor
        var redString = ChatColor.RED.toString();
        assertNotNull(redString);
        assertFalse(redString.isEmpty());

        assertTrue(redString.contains("§"));
    }

    @Test
    void constants_shouldHaveCorrectValues() {
        assertEquals('§', ChatColor.COLOR_CHAR);
        assertNotNull(ChatColor.HEX_PATTERN);

        assertTrue(ChatColor.HEX_PATTERN.matcher("&#FF0000").find());
        assertTrue(ChatColor.HEX_PATTERN.matcher("&#123abc").find());
        assertFalse(ChatColor.HEX_PATTERN.matcher("&FF0000").find()); // Missing #
        assertFalse(ChatColor.HEX_PATTERN.matcher("&#GG0000").find()); // Invalid hex
    }
}