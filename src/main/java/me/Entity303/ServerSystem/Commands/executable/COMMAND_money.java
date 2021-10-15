package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_money extends MessageUtils implements CommandExecutor {

    public COMMAND_money(ss plugin) {
        super(plugin);
    }

    private ss getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            if (args.length <= 0) {
                if (!(cs instanceof Player)) {
                    cs.sendMessage(this.getPrefix() + this.getSyntax("Money", label, cmd.getName(), cs, null));
                    return;
                }
                if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.money.self.required"))
                    if (!this.isAllowed(cs, "money.self.permission")) {
                        cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("money.self.permission")));
                        return;
                    }
                if (!this.getPlugin().getEconomyManager().hasAccount((OfflinePlayer) cs))
                    this.getPlugin().getEconomyManager().createAccount((OfflinePlayer) cs);
                cs.sendMessage(this.getPrefix() + this.getMessage("Money.Self", label, cmd.getName(), cs, null).replace("<BALANCE>", this.getPlugin().getEconomyManager().format(this.getPlugin().getEconomyManager().getMoneyAsNumber((Player) cs))));
                return;
            }
            if (!this.isAllowed(cs, "money.others")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("money.others")));
                return;
            }
            Player target = this.getPlayer(cs, args[0]);
            if (target == null) {
                cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
                return;
            }
            cs.sendMessage(this.getPrefix() + this.getMessage("Money.Others", label, cmd.getName(), cs, target).replace("<BALANCE>", this.getPlugin().getEconomyManager().format(this.getPlugin().getEconomyManager().getMoneyAsNumber(target))));
        });
        return true;
    }
}
