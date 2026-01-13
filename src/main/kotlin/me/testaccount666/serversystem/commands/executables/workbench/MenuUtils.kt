package me.testaccount666.serversystem.commands.executables.workbench

import me.testaccount666.serversystem.ServerSystem.Companion.serverVersion
import me.testaccount666.serversystem.utils.Version
import org.bukkit.entity.Player

/**
 * Utility class for opening various crafting menus for players.
 * Handles compatibility between legacy and modern Minecraft versions.
 */
object MenuUtils {
    private val _LEGACY_VERSION = serverVersion < Version("1.21.4")

    /**
     * Opens a workbench menu for the player.
     * 
     * @param player The player to open the menu for
     */
    fun openWorkbench(player: Player) {
        if (_LEGACY_VERSION) player.openWorkbench(player.location, true)
        else org.bukkit.inventory.MenuType.CRAFTING.create(player).open()
    }

    /**
     * Opens an anvil menu for the player.
     * 
     * @param player The player to open the menu for
     */
    fun openAnvil(player: Player) {
        if (_LEGACY_VERSION) player.openAnvil(player.location, true)
        else org.bukkit.inventory.MenuType.ANVIL.create(player).open()
    }

    /**
     * Opens a smithing table menu for the player.
     * 
     * @param player The player to open the menu for
     */
    fun openSmithing(player: Player) {
        if (_LEGACY_VERSION) player.openSmithingTable(player.location, true)
        else org.bukkit.inventory.MenuType.SMITHING.create(player).open()
    }

    /**
     * Opens a loom menu for the player.
     * 
     * @param player The player to open the menu for
     */
    fun openLoom(player: Player) {
        if (_LEGACY_VERSION) player.openLoom(player.location, true)
        else org.bukkit.inventory.MenuType.LOOM.create(player).open()
    }

    /**
     * Opens a grindstone menu for the player.
     * 
     * @param player The player to open the menu for
     */
    fun openGrindstone(player: Player) {
        if (_LEGACY_VERSION) player.openGrindstone(player.location, true)
        else org.bukkit.inventory.MenuType.GRINDSTONE.create(player).open()
    }

    /**
     * Opens a cartography table menu for the player.
     * 
     * @param player The player to open the menu for
     */
    fun openCartography(player: Player) {
        if (_LEGACY_VERSION) player.openCartographyTable(player.location, true)
        else org.bukkit.inventory.MenuType.CARTOGRAPHY_TABLE.create(player).open()
    }

    /**
     * Opens a stonecutter menu for the player.
     * 
     * @param player The player to open the menu for
     */
    fun openStonecutter(player: Player) {
        if (_LEGACY_VERSION) player.openStonecutter(player.location, true)
        else org.bukkit.inventory.MenuType.STONECUTTER.create(player).open()
    }
}