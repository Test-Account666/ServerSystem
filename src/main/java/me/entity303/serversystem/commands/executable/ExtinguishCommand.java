package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExtinguishCommand extends CommandUtils implements ICommandExecutorOverload {

    public ExtinguishCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Extinguish"));
                return true;
            }

            if (!this._plugin.GetPermissions().HasPermission(player, "extinguish.self")) {
                var permission = this._plugin.GetPermissions().GetPermission("extinguish.self");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }

            player.setFireTicks(0);

            player.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Extinguish.Self"));
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "extinguish.others")) {
            var permission = this._plugin.GetPermissions().GetPermission("extinguish.others");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        var target = this.GetPlayer(commandSender, arguments[0]);
        if (target == null)
            return true;

        target.setFireTicks(0);

        target.sendMessage(this._plugin.GetMessages().GetPrefix() +
                           this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Extinguish.Others.Target"));

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Extinguish.Others.Sender"));
        return true;
    }

    public String GetPrefix() {
        return this._plugin.GetMessages().GetPrefix();
    }
}
