package me.testaccount666.serversystem.commands.executables.balance;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.money.EconomyProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "balance", variants = "baltop")
public class CommandBalance extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().equalsIgnoreCase("baltop")) {
            executeBaltop(commandSender);
            return;
        }

        executeBalance(commandSender, command, label, arguments);
    }

    private void executeBaltop(User commandSender) {
        if (!checkBasePermission(commandSender, "Baltop.Use")) return;
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        var bankAccount = commandSender.getBankAccount();
        var topTen = bankAccount.getTopTen();

        if (topTen.isEmpty()) {
            command("Baltop.NoData", commandSender).build();
            return;
        }

        command("Baltop.Header", commandSender).prefix(false).build();

        var position = 1;
        for (var entry : topTen.entrySet()) {
            var playerUuid = entry.getKey();
            var balance = entry.getValue();
            var formattedBalance = ServerSystem.getInstance().getRegistry().getService(EconomyProvider.class).formatMoney(balance);
            var playerName = Bukkit.getOfflinePlayer(playerUuid).getName();
            playerName = playerName != null? playerName : "Unknown";

            var currentPosition = position;
            command("Baltop.Entry", commandSender).prefix(false).target(playerName)
                    .postModifier(message -> message
                            .replace("<POSITION>", String.valueOf(currentPosition))
                            .replace("<BALANCE>", formattedBalance))
                    .build();
            position++;
        }
    }

    private void executeBalance(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Balance.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments)) return;

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
        var formattedBalance = ServerSystem.getInstance().getRegistry().getService(EconomyProvider.class).formatMoney(balance);

        var messagePath = isSelf? "Balance.Success" : "Balance.SuccessOther";

        command(messagePath, commandSender)
                .target(targetPlayer.getName())
                .postModifier(message -> message.replace("<BALANCE>", formattedBalance)).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "Balance";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Balance.Use", false);
    }
}
