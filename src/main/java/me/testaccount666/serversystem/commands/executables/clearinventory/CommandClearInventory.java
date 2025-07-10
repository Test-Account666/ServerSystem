package me.testaccount666.serversystem.commands.executables.clearinventory;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "clearinventory")
public class CommandClearInventory extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "ClearInventory.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "ClearInventory.Other", targetPlayer.getName())) return;

        var inventory = targetPlayer.getInventory();
        inventory.clear();
        inventory.setContents(new ItemStack[0]);
        inventory.setArmorContents(new ItemStack[0]);
        inventory.setExtraContents(new ItemStack[0]);
        inventory.setStorageContents(new ItemStack[0]);

        var messagePath = isSelf? "ClearInventory.Success" : "ClearInventory.SuccessOther";
        command(messagePath, commandSender).target(targetPlayer.getName()).build();


        if (isSelf) return;

        command("ClearInventory.Success", targetUser)
                .sender(commandSender.getName().get()).target(targetPlayer.getName()).build();
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "ClearInventory.Use", false);
    }
}
