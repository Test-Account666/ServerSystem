package me.testaccount666.serversystem.commands.executables.inventorysee.offline;

import de.tr7zw.nbtapi.NBT;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.inventorysee.online.CommandInventorySee;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.general;

public class CommandOfflineInventorySee extends AbstractServerSystemCommand {
    public final InventoryLoader inventoryLoader;

    public CommandOfflineInventorySee(CommandInventorySee commandInventorySee) {
        if (!Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
            ServerSystem.getLog().warning("NBTAPI is not installed, Offline-InventorySee will not work!");
            inventoryLoader = null;
            return;
        }

        if (!NBT.preloadApi()) {
            ServerSystem.getLog().severe("Failed to load NBT-API!");
            inventoryLoader = null;
            return;
        }

        inventoryLoader = new InventoryLoader();
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (inventoryLoader == null) {

            general("CommandDisabled", commandSender).label(label)
                    .postModifier(message -> message.replace("<REASON>", "NBTAPI is not installed, Offline-InventorySee will not work!"));
            return;
        }

        if (!command.getName().equalsIgnoreCase("offlineinventorysee")) return;

        processOfflineInventorySee(commandSender, label, arguments);
    }

    public void processOfflineInventorySee(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "OfflineInventorySee.Use")) return;

        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        if (arguments.length < 1) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(null)).label(label).build();
            return;
        }

        var cachedUserOptional = ServerSystem.Instance.getRegistry().getService(UserManager.class).getUser(arguments[0]);
        if (cachedUserOptional.isEmpty()) {
            general("Offline.NeverPlayed", commandSender).target(arguments[0]).build();
            return;
        }

        var cachedUser = cachedUserOptional.get();
        if (cachedUser.isOnlineUser()) {
            general("Offline.NotOffline", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = cachedUser.getOfflineUser();
        var targetPlayer = targetUser.getPlayer();

        var inventoryOptional = inventoryLoader.loadOfflineInventory(targetPlayer);
        if (inventoryOptional.isEmpty()) {
            general("ErrorOccurred", commandSender).label(label).target(arguments[0]).build();
            return;
        }

        var inventory = inventoryOptional.get();
        commandSender.getPlayer().openInventory(inventory);
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "OfflineInventorySee";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "OfflineInventorySee.Use", false);
    }
}
