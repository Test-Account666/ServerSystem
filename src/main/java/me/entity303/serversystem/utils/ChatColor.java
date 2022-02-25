package me.entity303.serversystem.utils;

import com.google.common.collect.Maps;
import me.entity303.serversystem.main.ServerSystem;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

import java.awt.*;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ChatColor {
    BLACK('0', 0) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.BLACK;
        }
    },
    DARK_BLUE('1', 1) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.DARK_BLUE;
        }
    },
    DARK_GREEN('2', 2) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.DARK_GREEN;
        }
    },
    DARK_AQUA('3', 3) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.DARK_AQUA;
        }
    },
    DARK_RED('4', 4) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.DARK_RED;
        }
    },
    DARK_PURPLE('5', 5) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.DARK_PURPLE;
        }
    },
    GOLD('6', 6) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.GOLD;
        }
    },
    GRAY('7', 7) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.GRAY;
        }
    },
    DARK_GRAY('8', 8) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.DARK_GRAY;
        }
    },
    BLUE('9', 9) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.BLUE;
        }
    },
    GREEN('a', 10) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.GREEN;
        }
    },
    AQUA('b', 11) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.AQUA;
        }
    },
    RED('c', 12) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.RED;
        }
    },
    LIGHT_PURPLE('d', 13) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.LIGHT_PURPLE;
        }
    },
    YELLOW('e', 14) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.YELLOW;
        }
    },
    WHITE('f', 15) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.WHITE;
        }
    },
    MAGIC('k', 16, true) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.MAGIC;
        }
    },
    BOLD('l', 17, true) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.BOLD;
        }
    },
    STRIKETHROUGH('m', 18, true) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.STRIKETHROUGH;
        }
    },
    UNDERLINE('n', 19, true) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.UNDERLINE;
        }
    },
    ITALIC('o', 20, true) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.ITALIC;
        }
    },
    RESET('r', 21) {
        @Override
        public net.md_5.bungee.api.ChatColor asBungee() {
            return net.md_5.bungee.api.ChatColor.RESET;
        }
    };

    public static final char COLOR_CHAR = '§';
    public static final Pattern REPLACE_ALL_PATTERN;
    public static final Pattern REPLACE_ALL_RGB_PATTERN;
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '§' + "[0-9A-FK-OR]");
    private static final Map<Integer, ChatColor> BY_ID = Maps.newHashMap();
    private static final Map<Character, ChatColor> BY_CHAR = Maps.newHashMap();

    static {
        REPLACE_ALL_PATTERN = Pattern.compile("(&)?&([0-9a-fk-orA-FK-OR])");
        REPLACE_ALL_RGB_PATTERN = Pattern.compile("(&)?&#([0-9a-fA-F]{6})");
    }

    static {
        ChatColor[] var3;
        int var2 = (var3 = ChatColor.values()).length;

        for (int var1 = 0; var1 < var2; ++var1) {
            ChatColor color = var3[var1];
            ChatColor.BY_ID.put(color.intCode, color);
            ChatColor.BY_CHAR.put(color.code, color);
        }

    }

    private final int intCode;
    private final char code;
    private final boolean isFormat;
    private final String toString;

    ChatColor(char code, int intCode) {
        this(code, intCode, false);
    }

    ChatColor(char code, int intCode, boolean isFormat) {
        this.code = code;
        this.intCode = intCode;
        this.isFormat = isFormat;
        this.toString = new String(new char[]{'§', code});
    }

    /**
     * Code by EssentialsX, thanks <3
     * Download it here: https://www.spigotmc.org/resources/essentialsx.9089/
     *
     * @param input     = Text to translate colors
     * @param supported = ChatColors that are supported
     * @param rgb       = If rgb should be used
     * @return = Returns colored String
     */
    static String replaceColor(String input, Set supported, boolean rgb) {
        StringBuffer legacyBuilder = new StringBuffer();
        Matcher legacyMatcher = ChatColor.REPLACE_ALL_PATTERN.matcher(input);

        label58:
        while (legacyMatcher.find()) {
            boolean isEscaped = legacyMatcher.group(1) != null;
            if (!isEscaped) {
                char code = legacyMatcher.group(2).toLowerCase(Locale.ROOT).charAt(0);

                for (Object o : supported) {
                    org.bukkit.ChatColor color = (org.bukkit.ChatColor) o;
                    if (color.getChar() == code) {
                        legacyMatcher.appendReplacement(legacyBuilder, "§$2");
                        continue label58;
                    }
                }
            }

            legacyMatcher.appendReplacement(legacyBuilder, "&$2");
        }

        legacyMatcher.appendTail(legacyBuilder);
        if (!rgb) return legacyBuilder.toString();
        else {
            StringBuffer rgbBuilder = new StringBuffer();
            Matcher rgbMatcher = ChatColor.REPLACE_ALL_RGB_PATTERN.matcher(legacyBuilder.toString());

            while (true) {
                while (true) {
                    if (!rgbMatcher.find()) {
                        rgbMatcher.appendTail(rgbBuilder);
                        return rgbBuilder.toString();
                    }

                    boolean isEscaped = rgbMatcher.group(1) != null;
                    if (isEscaped) break;

                    try {
                        String hexCode = rgbMatcher.group(2);
                        rgbMatcher.appendReplacement(rgbBuilder, ChatColor.parseHexColor(hexCode));
                    } catch (NumberFormatException var9) {
                        break;
                    }
                }

                rgbMatcher.appendReplacement(rgbBuilder, "&#$2");
            }
        }
    }

    /**
     * Code by EssentialsX, thanks <3
     * Download it here: https://www.spigotmc.org/resources/essentialsx.9089/
     *
     * @param hexColor = The color that should be parsed
     * @return = Returns the color String
     * @throws NumberFormatException = If hexColor is an invalid hex
     */
    public static String parseHexColor(String hexColor) throws NumberFormatException {
        if (hexColor.startsWith("#")) hexColor = hexColor.substring(1);

        if (hexColor.length() != 6) throw new NumberFormatException("Invalid hex length");
        else {
            Color.decode("#" + hexColor);
            StringBuilder assembledColorCode = new StringBuilder();
            assembledColorCode.append("§x");
            char[] var2 = hexColor.toCharArray();
            int var3 = var2.length;

            for (char curChar : var2) assembledColorCode.append("§").append(curChar);

            return assembledColorCode.toString();
        }
    }

    public static ChatColor getByChar(char code) {
        return ChatColor.BY_CHAR.get(code);
    }

    public static ChatColor getByChar(String code) {
        Validate.notNull(code, "Code cannot be null");
        Validate.isTrue(code.length() > 0, "Code must have at least one char");
        return ChatColor.BY_CHAR.get(code.charAt(0));
    }

    private static String getVersion() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].replace("_", ".");
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
        return version;
    }

    public static String stripColor(String input) {
        return input == null ? null : ChatColor.STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        if (textToTranslate.contains("&#")) try {
            if (ServerSystem.getPlugin(ServerSystem.class).getVersionManager().isV116())
                return ChatColor.replaceColor(textToTranslate, Stream.of(org.bukkit.ChatColor.values()).collect(Collectors.toSet()), true);
        } catch (Exception ignored) {
        }
        char[] b = textToTranslate.toCharArray();

        for (int i = 0; i < b.length - 1; ++i)
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }

        return new String(b);
    }

    public static String getLastColors(String input) {
        StringBuilder result = new StringBuilder();
        int length = input.length();

        for (int index = length - 1; index > -1; --index) {
            char section = input.charAt(index);
            if (section == 167 && index < length - 1) {
                char c = input.charAt(index + 1);
                ChatColor color = ChatColor.getByChar(c);
                if (color != null) {
                    result.insert(0, color);
                    if (color.isColor() || color.equals(ChatColor.RESET)) break;
                }
            }
        }

        return result.toString();
    }

    public net.md_5.bungee.api.ChatColor asBungee() {
        return net.md_5.bungee.api.ChatColor.RESET;
    }

    public char getChar() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.toString;
    }

    public boolean isFormat() {
        return this.isFormat;
    }

    public boolean isColor() {
        return !this.isFormat && this != ChatColor.RESET;
    }
}
