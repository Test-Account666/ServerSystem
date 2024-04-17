package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EconomyCommand extends CommandUtils implements CommandExecutorOverload {

    public EconomyCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "economy.general")) {
            var permission = this.plugin.getPermissions().getPermission("economy.general");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Economy.General"));
            return true;
        }

        if (this.PermissionCheckFailed(arguments[0], commandSender, command, commandLabel))
            return true;

        if (arguments.length <= 2) {
            this.SendHelpCommand(arguments[0], commandSender, command, commandLabel);
            return true;
        }

        var target = this.player(arguments[1]);

        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[1]));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(arguments[2]);
        } catch (NumberFormatException ignored) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                     target.getName(), "Economy.Error.NotANumber")
                                                                                         .replace("<NUMBER>", arguments[2]));
            return true;
        }

        switch (arguments[0].toLowerCase()) {
            case "set" -> {
                this.getPlugin().getEconomyManager().setMoney(target, amount);

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                         target.getName(),
                                                                                                                         "Economy.Success.Set.Sender")
                                                                                             .replace("<AMOUNT>",
                                                                                                      this.getPlugin().getEconomyManager().format(amount)));
                if (target.isOnline())
                    target.getPlayer()
                          .sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                          .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                      target.getName(),
                                                                                                                      "Economy.Success.Set.Target")
                                                                                          .replace("<AMOUNT>",
                                                                                                   this.getPlugin().getEconomyManager().format(amount)));
            }
            case "give", "add" -> {
                this.getPlugin().getEconomyManager().addMoney(target, amount);
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                         target.getName(),
                                                                                                                         "Economy.Success.Give.Sender")
                                                                                             .replace("<AMOUNT>",
                                                                                                      this.getPlugin().getEconomyManager().format(amount)));
                if (target.isOnline())
                    target.getPlayer()
                          .sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                          .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                      target.getName(),
                                                                                                                      "Economy.Success.Give.Target")
                                                                                          .replace("<AMOUNT>",
                                                                                                   this.getPlugin().getEconomyManager().format(amount)));
            }
            case "revoke", "take", "remove" -> {
                this.getPlugin().getEconomyManager().removeMoney(target, amount);
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                         target.getName(),
                                                                                                                         "Economy.Success.Revoke.Sender")
                                                                                             .replace("<AMOUNT>",
                                                                                                      this.getPlugin().getEconomyManager().format(amount)));
                if (target.isOnline())
                    target.getPlayer()
                          .sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                          .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                      target.getName(),
                                                                                                                      "Economy.Success.Revoke.Target")
                                                                                          .replace("<AMOUNT>",
                                                                                                   this.getPlugin().getEconomyManager().format(amount)));
            }
            default -> commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Economy.General"));
        }
        return true;
    }

    private boolean PermissionCheckFailed(String argument, CommandSender commandSender, Command command, String commandLabel) {
        switch (argument.toLowerCase()) {
            case "set" -> {
                if (!this.plugin.getPermissions().hasPermission(commandSender, "economy.set")) {
                    var permission = this.plugin.getPermissions().getPermission("economy.set");
                    commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                    return false;
                }
            }
            case "give", "add" -> {
                if (!this.plugin.getPermissions().hasPermission(commandSender, "economy.give")) {
                    var permission = this.plugin.getPermissions().getPermission("economy.give");
                    commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                    return false;
                }
            }
            case "revoke", "take", "remove" -> {
                if (!this.plugin.getPermissions().hasPermission(commandSender, "economy.revoke")) {
                    var permission = this.plugin.getPermissions().getPermission("economy.revoke");
                    commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                    return false;
                }
            }
            default -> commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Economy.General"));
        }

        return true;
    }

    private void SendHelpCommand(String argument, CommandSender commandSender, Command command, String commandLabel) {
        switch (argument.toLowerCase()) {
            case "set" -> {
                var command3 = command.getName();
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command3, commandSender, null, "Economy.Set"));
            }
            case "add", "give" -> {
                var command2 = command.getName();
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command2, commandSender, null, "Economy.Give"));
            }
            case "take", "revoke", "remove" -> {
                var command1 = command.getName();
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getSyntax(commandLabel, command1, commandSender, null, "Economy.Revoke"));
            }
            default -> commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Economy.General"));
        }
    }

    private OfflinePlayer player(String name) {
        OfflinePlayer player = Bukkit.getPlayer(name);
        if (player == null)
            player = Bukkit.getOfflinePlayer(name);
        if (!this.plugin.getEconomyManager().hasAccount(player))
            return null;
        return player;
    }

    private ServerSystem getPlugin() {
        return this.plugin;
    }
}
