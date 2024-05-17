package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public MoneyCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        Bukkit.getScheduler().runTaskAsynchronously(this._plugin, () -> {
            if (arguments.length == 0) {
                if (!(commandSender instanceof Player)) {

                    commandSender.sendMessage(
                            this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Money"));
                    return;
                }
                if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.money.self.required"))
                    if (!this._plugin.GetPermissions().HasPermission(commandSender, "money.self.permission")) {
                        var permission = this._plugin.GetPermissions().GetPermission("money.self.permission");
                        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                        return;
                    }
                if (!this._plugin.GetEconomyManager().HasAccount((OfflinePlayer) commandSender))
                    this._plugin.GetEconomyManager().CreateAccount((OfflinePlayer) commandSender);

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                             .GetMessage(commandLabel, command, commandSender, null, "Money.Self")
                                                                                             .replace("<BALANCE>", this._plugin
                                                                                                                       .GetEconomyManager()
                                                                                                                       .Format(this._plugin
                                                                                                                                   .GetEconomyManager()
                                                                                                                                   .GetMoneyAsNumber(
                                                                                                                                           (Player) commandSender))));
                return;
            }

            if (!this._plugin.GetPermissions().HasPermission(commandSender, "money.others")) {
                var permission = this._plugin.GetPermissions().GetPermission("money.others");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return;
            }

            var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
            if (target == null) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
                return;
            }

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessage(commandLabel, command, commandSender, target, "Money.Others")
                                                                                         .replace("<BALANCE>", this._plugin
                                                                                                                   .GetEconomyManager()
                                                                                                                   .Format(this._plugin
                                                                                                                               .GetEconomyManager()
                                                                                                                               .GetMoneyAsNumber(target))));
        });
        return true;
    }

    private ServerSystem GetPlugin() {
        return this._plugin;
    }
}
