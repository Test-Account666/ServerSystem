package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand extends CommandUtils implements ICommandExecutorOverload {

    public PayCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.pay.required"))
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "pay.permission")) {
                var permission = this._plugin.GetPermissions().GetPermission("pay.permission");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }

        if (arguments.length <= 1) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Pay"));
            return true;
        }
        var target = this.GetPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        if (target == commandSender) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        if (!this._plugin.GetEconomyManager().HasAccount(target))
            this._plugin.GetEconomyManager().CreateAccount(target);

        double amount;
        try {
            amount = Double.parseDouble(arguments[1]);
        } catch (NumberFormatException ignored) {

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessage(commandLabel, command, commandSender, target,
                                                                                                     "Pay.NotANumber")
                                                                                         .replace("<NUMBER>", arguments[1]));
            return true;
        }
        if (commandSender instanceof Player) {
            if (this._plugin.GetEconomyManager().HasAccount((Player) commandSender))
                this._plugin.GetEconomyManager().CreateAccount((Player) commandSender);
            if (!this._plugin.GetEconomyManager().HasEnoughMoney((Player) commandSender, amount)) {

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Pay.NotEnough"));
                return true;
            }
            if (amount <= 0) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Pay.ToLessAmount"));
                return true;
            }

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                     "Pay.Success.Self")
                                                                                         .replace("<AMOUNT>",
                                                                                                  this._plugin.GetEconomyManager().Format(amount)));
            this._plugin.GetEconomyManager().MakeTransaction((Player) commandSender, target, amount);

            target.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                  .GetMessage(commandLabel, command, commandSender, target, "Pay.Success.Others")
                                                                                  .replace("<AMOUNT>", this._plugin.GetEconomyManager().Format(amount)));
            return true;
        }

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                 "Pay.Success.Self")
                                                                                     .replace("<AMOUNT>", this._plugin.GetEconomyManager().Format(amount)));
        this._plugin.GetEconomyManager().AddMoney(target, amount);

        target.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                              .GetMessage(commandLabel, command, commandSender, target, "Pay.Success.Others")
                                                                              .replace("<AMOUNT>", this._plugin.GetEconomyManager().Format(amount)));
        return true;
    }

    private ServerSystem GetPlugin() {
        return this._plugin;
    }
}
