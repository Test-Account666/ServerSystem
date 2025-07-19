package me.testaccount666.serversystem.commands.executables.enderchest;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.enderchest.offline.CommandOfflineEnderChest;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "enderchest", variants = "offlineenderchest")
public class CommandEnderChest extends AbstractServerSystemCommand {
    public final CommandOfflineEnderChest offlineEnderChest;

    public CommandEnderChest() {
        offlineEnderChest = new CommandOfflineEnderChest();
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().toLowerCase().startsWith("offline")) {
            offlineEnderChest.execute(commandSender, command, label, arguments);
            return;
        }

        executeEnderChestCommand(commandSender, arguments);
    }

    public void executeEnderChestCommand(User commandSender, String... arguments) {
        if (!checkBasePermission(commandSender, "EnderChest.Use")) return;

        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, arguments);

        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "EnderChest.Other", targetPlayer.getName())) return;

        commandSender.getPlayer().openInventory(targetPlayer.getEnderChest());
    }

    @Override
    public String getSyntaxPath(Command command) {
        if (command.getName().toLowerCase().startsWith("offline")) return offlineEnderChest.getSyntaxPath(command);
        return "EnderChest";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "EnderChest.Use", false);
    }
}
