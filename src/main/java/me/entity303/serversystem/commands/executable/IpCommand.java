package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Objects;

@ServerSystemCommand(name = "IP")
public class IpCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public IpCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "ip")) {
            var permission = this._plugin.GetPermissions().GetPermission("ip");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Ip"));
            return true;
        }
        var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        var ipAddress = Objects.requireNonNull(target.getAddress()).getAddress().toString().split("/")[1];

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Ip").replace("<IP>", ipAddress));
        return true;
    }
}
