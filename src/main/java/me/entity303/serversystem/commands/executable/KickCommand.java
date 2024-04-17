package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KickCommand extends CommandUtils implements CommandExecutorOverload {

    public KickCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "kick.use")) {
            var permission = this.plugin.getPermissions().getPermission("kick.use");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }
        if (arguments.length == 0) {

            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Kick"));
            return true;
        }

        var target = this.getPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        if (this.plugin.getPermissions().hasPermission(target, "kick.exempt", true)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Kick.CannotKick"));
            return true;
        }

        var reason = this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Kick.DefaultReason");

        if (arguments.length > 1)
            reason = IntStream.range(1, arguments.length).mapToObj(i -> arguments[i] + " ").collect(Collectors.joining());

        target.kickPlayer(this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Kick.Kick").replace("<REASON>", reason));

        commandSender.sendMessage(
                this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Kick.Success"));
        return true;
    }
}
