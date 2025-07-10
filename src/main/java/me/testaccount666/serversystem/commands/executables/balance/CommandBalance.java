package me.testaccount666.serversystem.commands.executables.balance;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "balance")
public class CommandBalance extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Balance.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);

        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "Balance.Other", targetPlayer.getName())) return;

        var balance = targetUser.getBankAccount().getBalance();
        var formattedBalance = ServerSystem.Instance.getEconomyManager().formatMoney(balance);

        var messagePath = isSelf? "Balance.Success" : "Balance.SuccessOther";

        command(messagePath, commandSender)
                .target(targetPlayer.getName())
                .modifier(message -> message.replace("<BALANCE>", formattedBalance)).build();
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Balance.Use", false);
    }
}
