package me.testaccount666.serversystem.commands.executables.disposal;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "disposal")
public class CommandDisposal extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Disposal.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();
        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "Disposal.Other", targetPlayer.getName())) return;

        targetPlayer.openInventory(Bukkit.createInventory(targetPlayer, InventoryType.CHEST.getDefaultSize() * 2, "§cTrash"));

        if (isSelf) return;
        command("Disposal.Success", targetUser).target(targetPlayer.getName()).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "Disposal";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Disposal.Use", false);
    }
}
