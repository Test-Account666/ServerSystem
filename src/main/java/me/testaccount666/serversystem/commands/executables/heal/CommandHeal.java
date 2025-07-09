package me.testaccount666.serversystem.commands.executables.heal;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "heal", variants = "feed")
public class CommandHeal extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().equalsIgnoreCase("heal")) {
            handleHealCommand(commandSender, label, arguments);
            return;
        }

        handleFeedCommand(commandSender, label, arguments);
    }

    private void handleFeedCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Feed.Use", label)) return;
        if (handleConsoleWithNoTarget(commandSender, label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "Feed.Other", targetPlayer.getName(), label)) return;

        targetPlayer.setFoodLevel(20);
        targetPlayer.setSaturation(20);

        var messagePath = isSelf? "Feed.Success" : "Feed.SuccessOther";

        sendCommandMessage(commandSender, messagePath, targetPlayer.getName(), label, null);

        if (isSelf) return;
        sendCommandMessage(targetUser, "Feed.Success", commandSender.getName().get(), label, null);
    }

    private void handleHealCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Heal.Use", label)) return;
        if (handleConsoleWithNoTarget(commandSender, label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "Heal.Other", targetPlayer.getName(), label)) return;

        targetPlayer.setHealth(targetPlayer.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        targetPlayer.setFoodLevel(20);
        targetPlayer.setSaturation(20);

        var messagePath = isSelf? "Heal.Success" : "Heal.SuccessOther";

        sendCommandMessage(commandSender, messagePath, targetPlayer.getName(), label, null);

        if (isSelf) return;
        sendCommandMessage(targetUser, "Heal.Success", commandSender.getName().get(), label, null);
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Heal.Use", false);
    }
}
