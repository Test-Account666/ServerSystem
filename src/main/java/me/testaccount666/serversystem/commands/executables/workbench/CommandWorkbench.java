package me.testaccount666.serversystem.commands.executables.workbench;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

//TODO: I dunno, this class feels kinda random because it's different from how I did the other commands
@SuppressWarnings("UnstableApiUsage")
@ServerSystemCommand(name = "workbench", variants = {"anvil", "smithing", "loom", "grindstone", "cartography", "stonecutter"})
public class CommandWorkbench extends AbstractServerSystemCommand {
    private final boolean _legacyVersion;

    private final Map<String, Consumer<Player>> _menuOpeners = Map.of(
            "Workbench", this::openWorkbench,
            "Anvil", this::openAnvil,
            "Smithing", this::openSmithing,
            "Loom", this::openLoom,
            "Grindstone", this::openGrindstone,
            "Cartography", this::openCartography,
            "Stonecutter", this::openStonecutter
    );

    public CommandWorkbench() {
        _legacyVersion = ServerSystem.getServerVersion().compareTo(new Version("1.21.4")) < 0;

        Bukkit.getLogger().log(Level.FINE, "(CommandWorkbench) Legacy version: ${_legacyVersion}");
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (commandSender instanceof ConsoleUser) {
            sendGeneralMessage(commandSender, "NotPlayer", null, label, null);
            return;
        }

        var player = commandSender.getPlayer();
        var commandName = capitalizeFirstLetter(command.getName());

        var permissionPath = "${commandName}.Use";
        if (!checkBasePermission(commandSender, permissionPath, label)) return;

        var opener = _menuOpeners.get(commandName);
        if (opener == null) throw new IllegalStateException("Unexpected command name: ${commandName}");

        opener.accept(player);
    }

    private String capitalizeFirstLetter(String input) {
        if (input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    private void openWorkbench(Player player) {
        if (_legacyVersion) player.openWorkbench(player.getLocation(), true);
        else org.bukkit.inventory.MenuType.CRAFTING.create(player).open();
    }

    private void openAnvil(Player player) {
        if (_legacyVersion) player.openAnvil(player.getLocation(), true);
        else org.bukkit.inventory.MenuType.ANVIL.create(player).open();
    }

    private void openSmithing(Player player) {
        if (_legacyVersion) player.openSmithingTable(player.getLocation(), true);
        else org.bukkit.inventory.MenuType.SMITHING.create(player).open();
    }

    private void openLoom(Player player) {
        if (_legacyVersion) player.openLoom(player.getLocation(), true);
        else org.bukkit.inventory.MenuType.LOOM.create(player).open();
    }

    private void openGrindstone(Player player) {
        if (_legacyVersion) player.openGrindstone(player.getLocation(), true);
        else org.bukkit.inventory.MenuType.GRINDSTONE.create(player).open();
    }

    private void openCartography(Player player) {
        if (_legacyVersion) player.openCartographyTable(player.getLocation(), true);
        else org.bukkit.inventory.MenuType.CARTOGRAPHY_TABLE.create(player).open();
    }

    private void openStonecutter(Player player) {
        if (_legacyVersion) player.openStonecutter(player.getLocation(), true);
        else org.bukkit.inventory.MenuType.STONECUTTER.create(player).open();
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        var permissionPath = switch (command.getName()) {
            case "workbench" -> "Workbench.Use";
            case "anvil" -> "Anvil.Use";
            case "smithing" -> "Smithing.Use";
            case "loom" -> "Loom.Use";
            case "grindstone" -> "Grindstone.Use";
            case "cartography" -> "Cartography.Use";
            case "stonecutter" -> "Stonecutter.Use";
            default -> null;
        };

        if (permissionPath == null) return false;

        return PermissionManager.hasCommandPermission(player, permissionPath, false);
    }
}
