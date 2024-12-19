package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static java.lang.Integer.parseInt;

@ServerSystemCommand(name = "Burn")
public class BurnCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public BurnCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "burn")) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("burn")));
            return true;
        }
        if (arguments.length <= 1) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command.getName(), commandSender, null, "Burn"));
            return true;
        }

        var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        try {
            target.setFireTicks(parseInt(arguments[1]) * 20);
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                       "Burn.Success")
                                                                                           .replace("<TIME>", arguments[1]));
        } catch (Exception ignored) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                       "Burn.NotNumber")
                                                                                           .replace("<NUMBER>", arguments[1]));
        }
        return true;
    }
}
