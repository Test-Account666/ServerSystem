package me.testaccount666.serversystem.commands.executables.inventorysee.offline;

import de.tr7zw.nbtapi.NBT;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.inventorysee.online.CommandInventorySee;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandOfflineInventorySee extends AbstractServerSystemCommand {
    public final InventoryLoader inventoryLoader;

    public CommandOfflineInventorySee(CommandInventorySee commandInventorySee) {
        if (!Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
            Bukkit.getLogger().warning("NBTAPI is not installed, Offline-InventorySee will not work!");
            inventoryLoader = null;
            return;
        }

        if (!NBT.preloadApi()) {
            Bukkit.getLogger().severe("Failed to load NBT-API!");
            inventoryLoader = null;
            return;
        }

        inventoryLoader = new InventoryLoader(commandInventorySee);
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (inventoryLoader == null) {
            sendGeneralMessage(commandSender, "CommandDisabled", null, label,
                    message -> message.replace("<REASON>", "NBTAPI is not installed, Offline-InventorySee will not work!"));
            return;
        }

        if (!command.getName().equalsIgnoreCase("offlineinventorysee")) return;

        processOfflineInventorySee(commandSender, label, arguments);
    }

    public void processOfflineInventorySee(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "OfflineInventorySee.Use", label)) return;

        if (commandSender instanceof ConsoleUser) {
            sendGeneralMessage(commandSender, "NotPlayer", null, label, null);
            return;
        }

        if (arguments.length < 1) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        var cachedUserOptional = ServerSystem.Instance.getUserManager().getUser(arguments[0]);
        if (cachedUserOptional.isEmpty()) {
            sendCommandMessage(commandSender, "OfflineInventorySee.NeverPlayed", arguments[0], label, null);
            return;
        }

        var cachedUser = cachedUserOptional.get();
        if (cachedUser.isOnlineUser()) {
            sendCommandMessage(commandSender, "OfflineInventorySee.NotOffline", arguments[0], label, null);
            return;
        }

        var targetUser = cachedUser.getOfflineUser();
        var targetPlayer = targetUser.getPlayer();

        var inventoryOptional = inventoryLoader.loadOfflineInventory(targetPlayer);
        if (inventoryOptional.isEmpty()) {
            sendGeneralMessage(commandSender, "ErrorOccurred", arguments[0], label, null);
            return;
        }

        var inventory = inventoryOptional.get();
        commandSender.getPlayer().openInventory(inventory);
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "OfflineInventorySee.Use", false);
    }
}
