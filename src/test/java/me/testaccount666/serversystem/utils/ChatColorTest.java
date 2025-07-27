package me.testaccount666.serversystem.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatColorTest {

    @Test
    void translateColor_shouldTranslateAmpersandCodes() {
        var input = "&aHello &bWorld";
        var result = ChatColor.translateColor(input);

        assertTrue(result.contains("§a"));
        assertTrue(result.contains("§b"));
        assertFalse(result.contains("&a"));
        assertFalse(result.contains("&b"));
    }

    @Test
    void translateColor_shouldHandleNullAndEmpty() {
        assertEquals("", ChatColor.translateColor(null));
        assertEquals("", ChatColor.translateColor(""));
        assertEquals("plain text", ChatColor.translateColor("plain text"));
    }

    @Test
    void translateAlternateColorCodes_shouldHandleNullAndEmpty() {
        assertEquals("", ChatColor.translateAlternateColorCodes('&', null));
        assertEquals("", ChatColor.translateAlternateColorCodes('&', ""));
        assertEquals("plain text", ChatColor.translateAlternateColorCodes('&', "plain text"));
    }

    @Test
    void translateAlternateColorCodes_shouldTranslateCustomColorChar() {
        var input = "%aHello %bWorld";
        var result = ChatColor.translateAlternateColorCodes('%', input);

        assertTrue(result.contains("§a"));
        assertTrue(result.contains("§b"));
        assertFalse(result.contains("%a"));
        assertFalse(result.contains("%b"));
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

    @Test
    void replaceHexColor_shouldHandleNullAndEmpty() {
        assertEquals("", ChatColor.replaceHexColor('&', null));
        assertEquals("", ChatColor.replaceHexColor('&', ""));
        assertEquals("plain text", ChatColor.replaceHexColor('&', "plain text"));
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

    @Test
    void convertHexToMinecraft_shouldHandleNullAndInvalid() {
        assertEquals("", ChatColor.convertHexToMinecraft(null));
        assertEquals("", ChatColor.convertHexToMinecraft(""));
        assertEquals("", ChatColor.convertHexToMinecraft("FF00")); // Too short
        assertEquals("", ChatColor.convertHexToMinecraft("FF0000AA")); // Too long

        // Invalid characters are processed as-is (no validation)
        var invalidResult = ChatColor.convertHexToMinecraft("GGGGGG");
        assertEquals("§x§g§g§g§g§g§g", invalidResult);
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

    @Test
    void stripColor_shouldHandleNull() {
        assertEquals("", ChatColor.stripColor(null));
    }

    @Test
    void stripColor_shouldRemoveColorCodes() {
        var input = "§aHello §bWorld §cTest";
        var result = ChatColor.stripColor(input);

        assertEquals("Hello World Test", result);
        assertFalse(result.contains("§"));
    }

    @Test
    void stripColor_shouldRemoveFormattingCodes() {
        var input = "§lBold §nUnderline §oItalic §rReset";
        var result = ChatColor.stripColor(input);

        assertEquals("Bold Underline Italic Reset", result);
        assertFalse(result.contains("§"));
    }

    @Test
    void stripColor_shouldHandlePlainText() {
        var input = "Plain text without colors";
        var result = ChatColor.stripColor(input);

        assertEquals(input, result);
    }

    @Test
    void stripColor_shouldHandleEmptyString() {
        assertEquals("", ChatColor.stripColor(""));
    }

    @Test
    void stripColor_shouldHandleMixedContent() {
        var input = "§aColored §btext §rwith §lformatting and plain text";
        var result = ChatColor.stripColor(input);

        assertEquals("Colored text with formatting and plain text", result);
        assertFalse(result.contains("§"));
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