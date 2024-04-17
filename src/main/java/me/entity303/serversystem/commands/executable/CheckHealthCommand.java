package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CheckHealthCommand extends CommandUtils implements CommandExecutorOverload {

    public CheckHealthCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "checkhealth")) {
            var permission = this.plugin.getPermissions().getPermission("checkhealth");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getSyntax(commandLabel, command.getName(), commandSender, null, "CheckHealth"));
            return true;
        }

        var target = this.getPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                 "CheckHealth")
                                                                                     .replace("<HEALTH>", String.valueOf(target.getHealth()))
                                                                                     .replace("<FOOD>", String.valueOf(target.getFoodLevel()))
                                                                                     .replace("<MAXHEALTH>", String.valueOf(
                                                                                             target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())));
        return true;
    }
}
