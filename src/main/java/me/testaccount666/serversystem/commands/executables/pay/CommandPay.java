package me.testaccount666.serversystem.commands.executables.pay;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractPlayerTargetingCommand;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

import java.math.BigDecimal;
import java.math.RoundingMode;

@ServerSystemCommand(name = "pay")
public class CommandPay extends AbstractPlayerTargetingCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Pay.Use", label)) return;
        if (handleConsoleWithNoTarget(commandSender, label, 1, arguments)) return;

        if (arguments.length < 2) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        var targetPlayerOptional = getTargetPlayer(commandSender, false, arguments);
        if (targetPlayerOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetPlayer = targetPlayerOptional.get();
        var targetUserOptional = validateAndGetUser(commandSender, targetPlayer, label, "Pay");
        if (targetUserOptional.isEmpty()) return;

        var targetUser = targetUserOptional.get();
        var isSelf = targetUser == commandSender;

        if (isSelf) {
            sendCommandMessage(commandSender, "Pay.CannotPaySelf", targetPlayer.getName(), label, null);
            return;
        }

        try {
            var amount = BigDecimal.valueOf(Double.parseDouble(arguments[1]));
            amount = amount.setScale(2, RoundingMode.HALF_UP);

            var bankAccount = commandSender.getBankAccount();

            if (!bankAccount.hasEnoughMoney(amount)) {
                sendCommandMessage(commandSender, "Pay.NotEnoughMoney", targetPlayer.getName(), label, null);
                return;
            }

            bankAccount.withdraw(amount);
            targetUser.getBankAccount().deposit(amount);

            var formattedAmount = ServerSystem.Instance.getEconomyManager().formatMoney(amount);
            sendCommandMessage(commandSender, "Pay.Success", targetPlayer.getName(), label, message -> message.replace("<AMOUNT>", formattedAmount));
            sendCommandMessage(targetUser, "Pay.SuccessOther", commandSender.getName().get(), label, message -> message.replace("<AMOUNT>", formattedAmount));
        } catch (NumberFormatException ignored) {
            sendCommandMessage(commandSender, "Pay.InvalidAmount", targetPlayer.getName(), label, null);
        }
    }
}
