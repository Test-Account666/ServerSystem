package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class COMMAND_economy extends MessageUtils implements CommandExecutor {

    public COMMAND_economy(ss plugin) {
        super(plugin);
    }

    private OfflinePlayer player(String name) {
        OfflinePlayer player = Bukkit.getPlayer(name);
        if (player == null) player = Bukkit.getOfflinePlayer(name);
        if (!this.plugin.getEconomyManager().hasAccount(player)) return null;
        return player;
    }

    private ss getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "economy.general")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("economy.general")));
            return true;
        }
        if (args.length <= 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Economy.General", label, cmd.getName(), cs, null));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "set":
                if (!this.isAllowed(cs, "economy.set")) {
                    cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("economy.set")));
                    return true;
                }
                break;
            case "give":
            case "add":
                if (!this.isAllowed(cs, "economy.give")) {
                    cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("economy.give")));
                    return true;
                }
                break;
            case "revoke":
            case "take":
                if (!this.isAllowed(cs, "economy.revoke")) {
                    cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("economy.revoke")));
                    return true;
                }
                break;
            default:
                cs.sendMessage(this.getPrefix() + this.getSyntax("Economy.General", label, cmd.getName(), cs, null));
                break;
        }
        if (args.length <= 2) {
            switch (args[0].toLowerCase()) {
                case "set":
                    cs.sendMessage(this.getPrefix() + this.getSyntax("Economy.Set", label, cmd.getName(), cs, null));
                    break;
                case "add":
                    cs.sendMessage(this.getPrefix() + this.getSyntax("Economy.Give", label, cmd.getName(), cs, null));
                    break;
                case "take":
                    cs.sendMessage(this.getPrefix() + this.getSyntax("Economy.Revoke", label, cmd.getName(), cs, null));
                    break;
                default:
                    cs.sendMessage(this.getPrefix() + this.getSyntax("Economy.General", label, cmd.getName(), cs, null));
                    break;
            }
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "set": {
                OfflinePlayer target = this.player(args[1]);

                if (target == null) {
                    cs.sendMessage(this.getPrefix() + this.getNoTarget(args[1]));
                    return true;
                }
                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException ignored) {
                    cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Economy.Error.NotANumber", label, cmd.getName(), cs, target.getName()).replace("<NUMBER>", args[2]));
                    return true;
                }
                this.getPlugin().getEconomyManager().setMoney(target, amount);
                cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Economy.Success.Set.Sender", label, cmd.getName(), cs, target.getName()).replace("<AMOUNT>", this.getPlugin().getEconomyManager().format(amount)));
                if (target.isOnline())
                    target.getPlayer().sendMessage(this.getPrefix() + this.getMessage("Economy.Success.Set.Target", label, cmd.getName(), cs, target.getPlayer()).replace("<AMOUNT>", this.getPlugin().getEconomyManager().format(amount)));
                break;
            }
            case "add": {
                OfflinePlayer target = this.player(args[1]);

                if (target == null) {
                    cs.sendMessage(this.getPrefix() + this.getNoTarget(args[1]));
                    return true;
                }
                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException ignored) {
                    cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Economy.Error.NotANumber", label, cmd.getName(), cs, target.getName()).replace("<NUMBER>", args[2]));
                    return true;
                }
                this.getPlugin().getEconomyManager().addMoney(target, amount);
                cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Economy.Success.Give.Sender", label, cmd.getName(), cs, target.getName()).replace("<AMOUNT>", this.getPlugin().getEconomyManager().format(amount)));
                if (target.isOnline())
                    target.getPlayer().sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Economy.Success.Give.Target", label, cmd.getName(), cs, target.getName()).replace("<AMOUNT>", this.getPlugin().getEconomyManager().format(amount)));
                break;
            }
            case "take": {
                OfflinePlayer target = this.player(args[1]);

                if (target == null) {
                    cs.sendMessage(this.getPrefix() + this.getNoTarget(args[1]));
                    return true;
                }
                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException ignored) {
                    cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Economy.Error.NotANumber", label, cmd.getName(), cs, target.getName()).replace("<NUMBER>", args[2]));
                    return true;
                }
                this.getPlugin().getEconomyManager().removeMoney(target, amount);
                cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Economy.Success.Revoke.Sender", label, cmd.getName(), cs, target.getName()).replace("<AMOUNT>", this.getPlugin().getEconomyManager().format(amount)));
                if (target.isOnline())
                    target.getPlayer().sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Economy.Success.Revoke.Target", label, cmd.getName(), cs, target.getName()).replace("<AMOUNT>", this.getPlugin().getEconomyManager().format(amount)));
                break;
            }
            default:
                cs.sendMessage(this.getPrefix() + this.getSyntax("Economy.General", label, cmd.getName(), cs, null));
                break;
        }
        return true;
    }
}
