package me.entity303.serversystem.utils;

import java.util.regex.Pattern;

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

    public static final char COLOR_CHAR = '§';
    public static final Pattern REPLACE_ALL_PATTERN;
    public static final String RGB_PATTERN;
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '§' + "[0-9A-FK-OR]");
    private static final Pattern SPLIT_PATTERN = Pattern.compile("");

    static {
        REPLACE_ALL_PATTERN = Pattern.compile("(&)?&([0-9a-fk-orA-FK-OR])");
        RGB_PATTERN = "(&)?&#([0-9a-fA-F]{6})";
    }

    private final org.bukkit.ChatColor _chatColor;

    ChatColor() {
        this._chatColor = org.bukkit.ChatColor.valueOf(this.name());
    }

    public static String TranslateAlternateColorCodes(char altColorChar, String textToTranslate) {
        if (textToTranslate.contains(altColorChar + "#")) {
            try {
                textToTranslate = ChatColor.ReplaceHexColor(altColorChar, textToTranslate);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return org.bukkit.ChatColor.translateAlternateColorCodes(altColorChar, textToTranslate);
    }

    public static String ReplaceHexColor(char altColorChar, String input) {
        var matcher = Pattern.compile(ChatColor.RGB_PATTERN.replace("&", String.valueOf(altColorChar))).matcher(input);
        while (matcher.find()) input = input.replace(matcher.group(), ChatColor.OfHex(matcher.group()));

        return input;
    }

    public static String OfHex(String input) {
        var inputBuilder = new StringBuilder("§x");
        var arr = SPLIT_PATTERN.split(input);
        for (var colorCode : arr) {
            if (colorCode.contains("#") || colorCode.contains("&")) continue;
            inputBuilder.append(org.bukkit.ChatColor.getByChar(colorCode.toLowerCase()));
        }

        input = inputBuilder.toString();
        return input;
    }

    @Override
    public String toString() {
        return this._chatColor.toString();
    }

    public org.bukkit.ChatColor GetChatColor() {
        return this._chatColor;
    }
}
