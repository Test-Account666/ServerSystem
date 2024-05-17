package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CheckHealthCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public CheckHealthCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "checkhealth")) {
            var permission = this._plugin.GetPermissions().GetPermission("checkhealth");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetSyntax(commandLabel, command.getName(), commandSender, null, "CheckHealth"));
            return true;
        }

        var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                 "CheckHealth")
                                                                                     .replace("<HEALTH>", String.valueOf(target.getHealth()))
                                                                                     .replace("<FOOD>", String.valueOf(target.getFoodLevel()))
                                                                                     .replace("<MAXHEALTH>", String.valueOf(
                                                                                             target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())));
        return true;
    }
}
