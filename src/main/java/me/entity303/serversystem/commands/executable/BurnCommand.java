package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static java.lang.Integer.parseInt;


public class BurnCommand extends CommandUtils implements CommandExecutorOverload {

    public BurnCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "burn")) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("burn")));
            return true;
        }
        if (arguments.length <= 1) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command.getName(), commandSender, null, "Burn"));
            return true;
        }

        var target = this.getPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        try {
            target.setFireTicks(parseInt(arguments[1]) * 20);
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                     "Burn.Success")
                                                                                         .replace("<TIME>", arguments[1]));
        } catch (Exception ignored) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                     "Burn.NotNumber")
                                                                                         .replace("<NUMBER>", arguments[1]));
        }
        return true;
    }
}
