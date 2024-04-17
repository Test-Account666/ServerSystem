package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand extends CommandUtils implements CommandExecutorOverload {

    public PayCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.pay.required"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "pay.permission")) {
                var permission = this.plugin.getPermissions().getPermission("pay.permission");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }

        if (arguments.length <= 1) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Pay"));
            return true;
        }
        var target = this.getPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        if (target == commandSender) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        if (!this.plugin.getEconomyManager().hasAccount(target))
            this.plugin.getEconomyManager().createAccount(target);

        double amount;
        try {
            amount = Double.parseDouble(arguments[1]);
        } catch (NumberFormatException ignored) {

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command, commandSender, target,
                                                                                                     "Pay.NotANumber")
                                                                                         .replace("<NUMBER>", arguments[1]));
            return true;
        }
        if (commandSender instanceof Player) {
            if (this.plugin.getEconomyManager().hasAccount((Player) commandSender))
                this.plugin.getEconomyManager().createAccount((Player) commandSender);
            if (!this.getPlugin().getEconomyManager().hasEnoughMoney((Player) commandSender, amount)) {

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Pay.NotEnough"));
                return true;
            }
            if (amount <= 0) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Pay.ToLessAmount"));
                return true;
            }

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                     "Pay.Success.Self")
                                                                                         .replace("<AMOUNT>",
                                                                                                  this.getPlugin().getEconomyManager().format(amount)));
            this.getPlugin().getEconomyManager().makeTransaction((Player) commandSender, target, amount);

            target.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                  .getMessage(commandLabel, command, commandSender, target, "Pay.Success.Others")
                                                                                  .replace("<AMOUNT>", this.getPlugin().getEconomyManager().format(amount)));
            return true;
        }

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                 "Pay.Success.Self")
                                                                                     .replace("<AMOUNT>", this.getPlugin().getEconomyManager().format(amount)));
        this.getPlugin().getEconomyManager().addMoney(target, amount);

        target.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                              .getMessage(commandLabel, command, commandSender, target, "Pay.Success.Others")
                                                                              .replace("<AMOUNT>", this.getPlugin().getEconomyManager().format(amount)));
        return true;
    }

    private ServerSystem getPlugin() {
        return this.plugin;
    }
}
