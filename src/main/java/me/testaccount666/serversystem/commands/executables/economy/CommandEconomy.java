package me.testaccount666.serversystem.commands.executables.economy;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "economy", tabCompleter = TabCompleterEconomy.class)
public class CommandEconomy extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Economy.Use")) return;

        if (arguments.length <= 2) {
            general("InvalidArguments", commandSender).target(label).build();
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, 1, false, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[1]).build();
            return;
        }

        var targetUser = targetUserOptional.get();

        BigDecimal amount;
        try {
            amount = new BigDecimal(arguments[2]);
        } catch (NumberFormatException ignored) {
            command("Economy.InvalidAmount", commandSender).build();
            return;
        }

        var economyOperation = arguments[0].toLowerCase();

        switch (economyOperation) {
            case "set" -> handleSetEconomy(commandSender, label, targetUser, amount);
            case "give", "add" -> handleGiveEconomy(commandSender, label, targetUser, amount);
            case "take", "remove" -> handleTakeEconomy(commandSender, label, targetUser, amount);
            default -> general("InvalidArguments", commandSender).target(label).build();
        }
    }

    private void handleSetEconomy(User commandSender, String label, User targetUser, BigDecimal amount) {
        if (!checkBasePermission(commandSender, "Economy.Set")) return;

        targetUser.getBankAccount().setBalance(amount);
        sendSuccess(commandSender, label, targetUser, amount, "Set");
    }

    private void handleGiveEconomy(User commandSender, String label, User targetUser, BigDecimal amount) {
        if (!checkBasePermission(commandSender, "Economy.Give")) return;

        targetUser.getBankAccount().deposit(amount);
        sendSuccess(commandSender, label, targetUser, amount, "Give");
    }

    private void handleTakeEconomy(User commandSender, String label, User targetUser, BigDecimal amount) {
        if (!checkBasePermission(commandSender, "Economy.Take")) return;

        targetUser.getBankAccount().withdraw(amount);
        sendSuccess(commandSender, label, targetUser, amount, "Take");
    }

    public void sendSuccess(User commandSender, String label, User targetUser, BigDecimal amount, String key) {
        var formattedAmount = ServerSystem.Instance.getEconomyManager().formatMoney(amount);
        UnaryOperator<String> modifier = message -> message.replace("<AMOUNT>", formattedAmount);

        command("Economy.${key}.Success", commandSender)
                .target(targetUser.getName().get()).modifier(modifier).build();

        command("Economy.${key}.SuccessOther", targetUser)
                .sender(commandSender.getName().get()).target(targetUser.getName().get())
                .modifier(modifier).build();
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Economy.Use", false);
    }
}
