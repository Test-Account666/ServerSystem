package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KickCommand extends CommandUtils implements ICommandExecutorOverload {

    public KickCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "kick.use")) {
            var permission = this._plugin.GetPermissions().GetPermission("kick.use");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }
        if (arguments.length == 0) {

            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Kick"));
            return true;
        }

        var target = this.GetPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        if (this._plugin.GetPermissions().HasPermission(target, "kick.exempt", true)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Kick.CannotKick"));
            return true;
        }

        var reason = this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Kick.DefaultReason");

        if (arguments.length > 1)
            reason = IntStream.range(1, arguments.length).mapToObj(index -> arguments[index] + " ").collect(Collectors.joining());

        target.kickPlayer(this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Kick.Kick").replace("<REASON>", reason));

        commandSender.sendMessage(
                this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Kick.Success"));
        return true;
    }
}
