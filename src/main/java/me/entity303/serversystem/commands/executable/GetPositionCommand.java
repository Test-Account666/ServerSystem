package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class GetPositionCommand extends CommandUtils implements CommandExecutorOverload {

    public GetPositionCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "GetPos"));
            return true;
        }

        if (arguments.length == 0) {
            if (!this.plugin.getPermissions().hasPermission(commandSender, "getpos.self", true)) {
                var permissionMessage = this.plugin.getPermissions().getPermission("getpos.self");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permissionMessage));
                return true;
            }
            this.sendPositionMessage(commandSender, player, commandLabel, command);
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "getpos.others", true)) {
            var permissionMessage = this.plugin.getPermissions().getPermission("getpos.others");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permissionMessage));
            return true;
        }

        var target = this.getPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        this.sendPositionMessage(commandSender, target, commandLabel, command);
        return true;
    }

    private void sendPositionMessage(CommandSender sender, Player target, String commandLabel, Command command) {
        var x = this.formatCoordinate(target.getLocation().getX());
        var y = this.formatCoordinate(target.getLocation().getY());
        var z = this.formatCoordinate(target.getLocation().getZ());
        var world = Objects.requireNonNull(target.getLocation().getWorld()).getName();

        var message = this.plugin.getMessages()
                                 .getMessage(commandLabel, command, sender, target, target.equals(sender)? "GetPos.Self" : "GetPos.Others")
                                 .replace("<X>", x)
                                 .replace("<Y>", y)
                                 .replace("<Z>", z)
                                 .replace("<WORLD>", world);
        sender.sendMessage(this.plugin.getMessages().getPrefix() + message);
    }

    private String formatCoordinate(double coordinate) {
        var formatted = String.valueOf(coordinate);
        var parts = formatted.split("\\.");
        if (parts.length > 1 && parts[1].length() > 2)
            formatted = parts[0] + "." + parts[1].substring(0, 2);
        return formatted;
    }
}



