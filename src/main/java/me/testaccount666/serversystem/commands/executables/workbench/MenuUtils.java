package me.testaccount666.serversystem.commands.executables.workbench;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.utils.Version;
import org.bukkit.entity.Player;

/**
 * Utility class for opening various crafting menus for players.
 * Handles compatibility between legacy and modern Minecraft versions.
 */
@SuppressWarnings("UnstableApiUsage")
public class MenuUtils {
    private static final boolean _LEGACY_VERSION = ServerSystem.Companion.getServerVersion().compareTo(new Version("1.21.4")) < 0;

    /**
     * Opens a workbench menu for the player.
     *
     * @param player The player to open the menu for
     */
    public static void openWorkbench(Player player) {
        if (_LEGACY_VERSION) player.openWorkbench(player.getLocation(), true);
        else org.bukkit.inventory.MenuType.CRAFTING.create(player).open();
    }

    /**
     * Opens an anvil menu for the player.
     *
     * @param player The player to open the menu for
     */
    public static void openAnvil(Player player) {
        if (_LEGACY_VERSION) player.openAnvil(player.getLocation(), true);
        else org.bukkit.inventory.MenuType.ANVIL.create(player).open();
    }

    /**
     * Opens a smithing table menu for the player.
     *
     * @param player The player to open the menu for
     */
    public static void openSmithing(Player player) {
        if (_LEGACY_VERSION) player.openSmithingTable(player.getLocation(), true);
        else org.bukkit.inventory.MenuType.SMITHING.create(player).open();
    }

    /**
     * Opens a loom menu for the player.
     *
     * @param player The player to open the menu for
     */
    public static void openLoom(Player player) {
        if (_LEGACY_VERSION) player.openLoom(player.getLocation(), true);
        else org.bukkit.inventory.MenuType.LOOM.create(player).open();
    }

    /**
     * Opens a grindstone menu for the player.
     *
     * @param player The player to open the menu for
     */
    public static void openGrindstone(Player player) {
        if (_LEGACY_VERSION) player.openGrindstone(player.getLocation(), true);
        else org.bukkit.inventory.MenuType.GRINDSTONE.create(player).open();
    }

    /**
     * Opens a cartography table menu for the player.
     *
     * @param player The player to open the menu for
     */
    public static void openCartography(Player player) {
        if (_LEGACY_VERSION) player.openCartographyTable(player.getLocation(), true);
        else org.bukkit.inventory.MenuType.CARTOGRAPHY_TABLE.create(player).open();
    }

    /**
     * Opens a stonecutter menu for the player.
     *
     * @param player The player to open the menu for
     */
    public static void openStonecutter(Player player) {
        if (_LEGACY_VERSION) player.openStonecutter(player.getLocation(), true);
        else org.bukkit.inventory.MenuType.STONECUTTER.create(player).open();
    }
}