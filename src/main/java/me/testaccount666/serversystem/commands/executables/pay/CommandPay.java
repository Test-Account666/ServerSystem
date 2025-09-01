package me.testaccount666.serversystem.commands.executables.pay;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.money.EconomyProvider;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "pay")
public class CommandPay extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Pay.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, 1, arguments)) return;

        if (arguments.length < 2) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
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

        if (isSelf) {
            command("Pay.CannotPaySelf", commandSender).build();
            return;
        }

        try {
            var amount = BigDecimal.valueOf(Double.parseDouble(arguments[1]));
            amount = amount.setScale(2, RoundingMode.HALF_UP);

            var bankAccount = commandSender.getBankAccount();

            if (!bankAccount.hasEnoughMoney(amount)) {
                command("Pay.NotEnoughMoney", commandSender).target(targetPlayer.getName()).build();
                return;
            }

            bankAccount.withdraw(amount);
            targetUser.getBankAccount().deposit(amount);

            var formattedAmount = ServerSystem.Instance.getRegistry().getService(EconomyProvider.class).formatMoney(amount);


            command("Pay.Success", commandSender).target(targetPlayer.getName())
                    .postModifier(message -> message.replace("<AMOUNT>", formattedAmount)).build();

            command("Pay.SuccessOther", targetUser).target(targetPlayer.getName()).sender(commandSender.getName().get())
                    .postModifier(message -> message.replace("<AMOUNT>", formattedAmount)).build();
        } catch (NumberFormatException ignored) {
            command("Pay.InvalidAmount", commandSender).target(targetPlayer.getName()).build();
        }
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "Pay";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Pay.Use", false);
    }
}
