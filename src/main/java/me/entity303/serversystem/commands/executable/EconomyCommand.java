package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EconomyCommand extends CommandUtils implements ICommandExecutorOverload {

    public EconomyCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "economy.general")) {
            var permission = this._plugin.GetPermissions().GetPermission("economy.general");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Economy.General"));
            return true;
        }

        if (this.PermissionCheckFailed(arguments[0], commandSender, command, commandLabel))
            return true;

        if (arguments.length <= 2) {
            this.SendHelpCommand(arguments[0], commandSender, command, commandLabel);
            return true;
        }

        var target = this.Player(arguments[1]);

        if (target == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[1]));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(arguments[2]);
        } catch (NumberFormatException ignored) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                     target.getName(), "Economy.Error.NotANumber")
                                                                                         .replace("<NUMBER>", arguments[2]));
            return true;
        }

        switch (arguments[0].toLowerCase()) {
            case "set" -> {
                this._plugin.GetEconomyManager().SetMoney(target, amount);

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                             .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                         target.getName(),
                                                                                                                         "Economy.Success.Set.Sender")
                                                                                             .replace("<AMOUNT>",
                                                                                                      this._plugin.GetEconomyManager().Format(amount)));
                if (target.isOnline())
                    target.getPlayer()
                          .sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                          .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                      target.getName(),
                                                                                                                      "Economy.Success.Set.Target")
                                                                                          .replace("<AMOUNT>",
                                                                                                   this._plugin.GetEconomyManager().Format(amount)));
            }
            case "give", "add" -> {
                this._plugin.GetEconomyManager().AddMoney(target, amount);
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                             .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                         target.getName(),
                                                                                                                         "Economy.Success.Give.Sender")
                                                                                             .replace("<AMOUNT>",
                                                                                                      this._plugin.GetEconomyManager().Format(amount)));
                if (target.isOnline())
                    target.getPlayer()
                          .sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                          .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                      target.getName(),
                                                                                                                      "Economy.Success.Give.Target")
                                                                                          .replace("<AMOUNT>",
                                                                                                   this._plugin.GetEconomyManager().Format(amount)));
            }
            case "revoke", "take", "remove" -> {
                this._plugin.GetEconomyManager().RemoveMoney(target, amount);
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                             .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                         target.getName(),
                                                                                                                         "Economy.Success.Revoke.Sender")
                                                                                             .replace("<AMOUNT>",
                                                                                                      this._plugin.GetEconomyManager().Format(amount)));
                if (target.isOnline())
                    target.getPlayer()
                          .sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                          .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                      target.getName(),
                                                                                                                      "Economy.Success.Revoke.Target")
                                                                                          .replace("<AMOUNT>",
                                                                                                   this._plugin.GetEconomyManager().Format(amount)));
            }
            default -> commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Economy.General"));
        }
        return true;
    }

    private boolean PermissionCheckFailed(String argument, CommandSender commandSender, Command command, String commandLabel) {
        switch (argument.toLowerCase()) {
            case "set" -> {
                if (!this._plugin.GetPermissions().HasPermission(commandSender, "economy.set")) {
                    var permission = this._plugin.GetPermissions().GetPermission("economy.set");
                    commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                    return false;
                }
            }
            case "give", "add" -> {
                if (!this._plugin.GetPermissions().HasPermission(commandSender, "economy.give")) {
                    var permission = this._plugin.GetPermissions().GetPermission("economy.give");
                    commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                    return false;
                }
            }
            case "revoke", "take", "remove" -> {
                if (!this._plugin.GetPermissions().HasPermission(commandSender, "economy.revoke")) {
                    var permission = this._plugin.GetPermissions().GetPermission("economy.revoke");
                    commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                    return false;
                }
            }
            default -> commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Economy.General"));
        }

        return true;
    }

    private void SendHelpCommand(String argument, CommandSender commandSender, Command command, String commandLabel) {
        switch (argument.toLowerCase()) {
            case "set" -> {
                var command3 = command.getName();
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command3, commandSender, null, "Economy.Set"));
            }
            case "add", "give" -> {
                var command2 = command.getName();
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command2, commandSender, null, "Economy.Give"));
            }
            case "take", "revoke", "remove" -> {
                var command1 = command.getName();
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetSyntax(commandLabel, command1, commandSender, null, "Economy.Revoke"));
            }
            default -> commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Economy.General"));
        }
    }

    private OfflinePlayer Player(String name) {
        OfflinePlayer player = Bukkit.getPlayer(name);
        if (player == null)
            player = Bukkit.getOfflinePlayer(name);
        if (!this._plugin.GetEconomyManager().HasAccount(player))
            return null;
        return player;
    }

}
