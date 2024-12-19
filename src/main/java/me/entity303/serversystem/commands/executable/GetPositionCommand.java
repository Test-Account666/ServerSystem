package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

@ServerSystemCommand(name = "GetPosition")
public class GetPositionCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public GetPositionCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "GetPos"));
            return true;
        }

        if (arguments.length == 0) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "getpos.self", true)) {
                var permissionMessage = this._plugin.GetPermissions().GetPermission("getpos.self");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permissionMessage));
                return true;
            }
            this.SendPositionMessage(commandSender, player, commandLabel, command);
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "getpos.others", true)) {
            var permissionMessage = this._plugin.GetPermissions().GetPermission("getpos.others");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permissionMessage));
            return true;
        }

        var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        this.SendPositionMessage(commandSender, target, commandLabel, command);
        return true;
    }

    private void SendPositionMessage(CommandSender sender, Player target, String commandLabel, Command command) {
        var xCoordinate = this.FormatCoordinate(target.getLocation().getX());
        var yCoordinate = this.FormatCoordinate(target.getLocation().getY());
        var zCoordinate = this.FormatCoordinate(target.getLocation().getZ());
        var world = Objects.requireNonNull(target.getLocation().getWorld()).getName();

        var message = this._plugin.GetMessages()
                                  .GetMessage(commandLabel, command, sender, target, target.equals(sender)? "GetPos.Self" : "GetPos.Others")
                                  .replace("<X>", xCoordinate)
                                  .replace("<Y>", yCoordinate)
                                  .replace("<Z>", zCoordinate)
                                  .replace("<WORLD>", world);
        sender.sendMessage(this._plugin.GetMessages().GetPrefix() + message);
    }

    private String FormatCoordinate(double coordinate) {
        var formatted = String.valueOf(coordinate);
        var parts = formatted.split("\\.");
        if (parts.length > 1 && parts[1].length() > 2) formatted = parts[0] + "." + parts[1].substring(0, 2);
        return formatted;
    }
}



