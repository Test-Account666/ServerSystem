package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_pay extends MessageUtils implements CommandExecutor {

    public COMMAND_pay(ss plugin) {
        super(plugin);
    }

    private ss getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.pay.required"))
            if (!this.isAllowed(cs, "pay.permission")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("pay.permission")));
                return true;
            }
        if (args.length <= 1) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Pay", label, cmd.getName(), cs, null));
            return true;
        }
        Player target = this.getPlayer(cs, args[0]);
        if (target == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }

        if (target == cs) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }

        if (!this.plugin.getEconomyManager().hasAccount(target))
            this.plugin.getEconomyManager().createAccount(target);

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException ignored) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Pay.NotANumber", label, cmd.getName(), cs, target).replace("<NUMBER>", args[1]));
            return true;
        }
        if (cs instanceof Player) {
            if (this.plugin.getEconomyManager().hasAccount((Player) cs))
                this.plugin.getEconomyManager().createAccount((Player) cs);
            if (!this.getPlugin().getEconomyManager().hasEnoughMoney((Player) cs, amount)) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Pay.NotEnough", label, cmd.getName(), cs, target));
                return true;
            }
            if (amount <= 0) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Pay.ToLessAmount", label, cmd.getName(), cs, target));
                return true;
            }
            cs.sendMessage(this.getPrefix() + this.getMessage("Pay.Success.Self", label, cmd.getName(), cs, target).replace("<AMOUNT>", this.getPlugin().getEconomyManager().format(amount)));
            this.getPlugin().getEconomyManager().makeTransaction((Player) cs, target, amount);
            target.sendMessage(this.getPrefix() + this.getMessage("Pay.Success.Others", label, cmd.getName(), cs, target).replace("<AMOUNT>", this.getPlugin().getEconomyManager().format(amount)));
            return true;
        }
        cs.sendMessage(this.getPrefix() + this.getMessage("Pay.Success.Self", label, cmd.getName(), cs, target).replace("<AMOUNT>", this.getPlugin().getEconomyManager().format(amount)));
        this.getPlugin().getEconomyManager().addMoney(target, amount);
        target.sendMessage(this.getPrefix() + this.getMessage("Pay.Success.Others", label, cmd.getName(), cs, target).replace("<AMOUNT>", this.getPlugin().getEconomyManager().format(amount)));
        return true;
    }
}
