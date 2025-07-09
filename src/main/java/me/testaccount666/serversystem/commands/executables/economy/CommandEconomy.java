package me.testaccount666.serversystem.commands.executables.economy;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

@ServerSystemCommand(name = "economy", tabCompleter = TabCompleterEconomy.class)
public class CommandEconomy extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Economy.Use", label)) return;

        if (arguments.length <= 2) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, 1, false, arguments);
        if (targetUserOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[1]);
            return;
        }

        var targetUser = targetUserOptional.get();

        BigDecimal amount;
        try {
            amount = new BigDecimal(arguments[2]);
        } catch (NumberFormatException ignored) {
            sendCommandMessage(commandSender, "Economy.InvalidAmount", targetUser.getName().get(), label, null);
            return;
        }

        var economyOperation = arguments[0].toLowerCase();

        switch (economyOperation) {
            case "set" -> handleSetEconomy(commandSender, label, targetUser, amount);
            case "give", "add" -> handleGiveEconomy(commandSender, label, targetUser, amount);
            case "take", "remove" -> handleTakeEconomy(commandSender, label, targetUser, amount);
            default -> sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
        }
    }

    private void handleSetEconomy(User commandSender, String label, User targetUser, BigDecimal amount) {
        if (!checkBasePermission(commandSender, "Economy.Set", label)) return;

        targetUser.getBankAccount().setBalance(amount);

        sendCommandMessage(commandSender, "Economy.Set.Success", targetUser.getName().get(), label,
                message -> message.replace("<AMOUNT>", ServerSystem.Instance.getEconomyManager().formatMoney(amount)));

        sendCommandMessage(targetUser, "Economy.Set.SuccessOther", commandSender.getName().get(), label,
                message -> message.replace("<AMOUNT>", ServerSystem.Instance.getEconomyManager().formatMoney(amount)));
    }

    private void handleGiveEconomy(User commandSender, String label, User targetUser, BigDecimal amount) {
        if (!checkBasePermission(commandSender, "Economy.Give", label)) return;

        targetUser.getBankAccount().deposit(amount);

        sendCommandMessage(commandSender, "Economy.Give.Success", targetUser.getName().get(), label,
                message -> message.replace("<AMOUNT>", ServerSystem.Instance.getEconomyManager().formatMoney(amount)));

        sendCommandMessage(targetUser, "Economy.Give.SuccessOther", commandSender.getName().get(), label,
                message -> message.replace("<AMOUNT>", ServerSystem.Instance.getEconomyManager().formatMoney(amount)));
    }

    private void handleTakeEconomy(User commandSender, String label, User targetUser, BigDecimal amount) {
        if (!checkBasePermission(commandSender, "Economy.Take", label)) return;

        targetUser.getBankAccount().withdraw(amount);

        sendCommandMessage(commandSender, "Economy.Take.Success", targetUser.getName().get(), label,
                message -> message.replace("<AMOUNT>", ServerSystem.Instance.getEconomyManager().formatMoney(amount)));

        sendCommandMessage(targetUser, "Economy.Take.SuccessOther", commandSender.getName().get(), label,
                message -> message.replace("<AMOUNT>", ServerSystem.Instance.getEconomyManager().formatMoney(amount)));
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Economy.Use", false);
    }
}
