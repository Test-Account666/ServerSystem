package me.testaccount666.serversystem.commands.executables.balance;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "balance")
public class CommandBalance extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Balance.Use", label)) return;
        if (handleConsoleWithNoTarget(commandSender, label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);

        if (targetUserOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "Balance.Other", targetPlayer.getName(), label)) return;

        var balance = targetUser.getBankAccount().getBalance();
        var formattedBalance = ServerSystem.Instance.getEconomyManager().formatMoney(balance);

        var messagePath = isSelf? "Balance.Success" : "Balance.SuccessOther";

        sendCommandMessage(commandSender, messagePath, targetPlayer.getName(), label,
                message -> message.replace("<BALANCE>", formattedBalance));
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Balance.Use", false);
    }
}
