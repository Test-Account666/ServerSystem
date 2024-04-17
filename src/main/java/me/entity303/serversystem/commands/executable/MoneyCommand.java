package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand extends CommandUtils implements CommandExecutorOverload {

    public MoneyCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            if (arguments.length == 0) {
                if (!(commandSender instanceof Player)) {

                    commandSender.sendMessage(
                            this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Money"));
                    return;
                }
                if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.money.self.required"))
                    if (!this.plugin.getPermissions().hasPermission(commandSender, "money.self.permission")) {
                        var permission = this.plugin.getPermissions().getPermission("money.self.permission");
                        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                        return;
                    }
                if (!this.getPlugin().getEconomyManager().hasAccount((OfflinePlayer) commandSender))
                    this.getPlugin().getEconomyManager().createAccount((OfflinePlayer) commandSender);

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessage(commandLabel, command, commandSender, null, "Money.Self")
                                                                                             .replace("<BALANCE>", this.getPlugin()
                                                                                                                       .getEconomyManager()
                                                                                                                       .format(this.getPlugin()
                                                                                                                                   .getEconomyManager()
                                                                                                                                   .getMoneyAsNumber(
                                                                                                                                           (Player) commandSender))));
                return;
            }

            if (!this.plugin.getPermissions().hasPermission(commandSender, "money.others")) {
                var permission = this.plugin.getPermissions().getPermission("money.others");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return;
            }

            var target = this.getPlayer(commandSender, arguments[0]);
            if (target == null) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
                return;
            }

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command, commandSender, target, "Money.Others")
                                                                                         .replace("<BALANCE>", this.getPlugin()
                                                                                                                   .getEconomyManager()
                                                                                                                   .format(this.getPlugin()
                                                                                                                               .getEconomyManager()
                                                                                                                               .getMoneyAsNumber(target))));
        });
        return true;
    }

    private ServerSystem getPlugin() {
        return this.plugin;
    }
}
