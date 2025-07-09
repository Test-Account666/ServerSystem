package me.testaccount666.serversystem.commands.executables.clearinventory;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@ServerSystemCommand(name = "clearinventory")
public class CommandClearInventory extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "ClearInventory.Use", label)) return;
        if (handleConsoleWithNoTarget(commandSender, label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "ClearInventory.Other", targetPlayer.getName(), label)) return;

        var inventory = targetPlayer.getInventory();
        inventory.clear();
        inventory.setContents(new ItemStack[0]);
        inventory.setArmorContents(new ItemStack[0]);
        inventory.setExtraContents(new ItemStack[0]);
        inventory.setStorageContents(new ItemStack[0]);

        var messagePath = isSelf? "ClearInventory.Success" : "ClearInventory.SuccessOther";

        sendCommandMessage(commandSender, messagePath, targetPlayer.getName(), label, null);

        if (isSelf) return;
        sendCommandMessage(targetUser, "ClearInventory.Success", commandSender.getName().get(), label, null);
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "ClearInventory.Use", false);
    }
}
