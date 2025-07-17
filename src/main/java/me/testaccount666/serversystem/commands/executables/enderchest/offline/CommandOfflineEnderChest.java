package me.testaccount666.serversystem.commands.executables.enderchest.offline;

import de.tr7zw.nbtapi.NBT;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.general;

public class CommandOfflineEnderChest extends AbstractServerSystemCommand {
    public final EnderChestLoader enderChestLoader;

    public CommandOfflineEnderChest() {
        if (!Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
            ServerSystem.getLog().warning("NBTAPI is not installed, Offline-EnderChest will not work!");
            enderChestLoader = null;
            return;
        }

        if (!NBT.preloadApi()) {
            ServerSystem.getLog().severe("Failed to load NBT-API!");
            enderChestLoader = null;
            return;
        }

        enderChestLoader = new EnderChestLoader();
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        if (arguments.length < 1) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
            return;
        }

        executeEnderChestCommand(commandSender, arguments);
    }

    public void executeEnderChestCommand(User commandSender, String... arguments) {
        if (!checkBasePermission(commandSender, "OfflineEnderChest.Use")) return;

        var cachedUserOptional = ServerSystem.Instance.getUserManager().getUser(arguments[0]);
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

        var inventoryOptional = enderChestLoader.loadOfflineInventory(targetPlayer);
        if (inventoryOptional.isEmpty()) {
            general("ErrorOccurred", commandSender).target(arguments[0]).build();
            return;
        }

        var inventory = inventoryOptional.get();
        commandSender.getPlayer().openInventory(inventory);
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "OfflineEnderChest";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "OfflineEnderChest.Use", false);
    }
}
