package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DeleteKitCommand extends CommandUtils implements ICommandExecutorOverload {

    public DeleteKitCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "deletekit")) {
            var permission = this._plugin.GetPermissions().GetPermission("deletekit");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "DeleteKit"));
            return true;
        }

        if (!this._plugin.GetKitsManager().DoesKitExist(arguments[0])) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessage(commandLabel, command, commandSender, null,
                                                                                                     "DeleteKit.DoesntExist")
                                                                                         .replace("<KIT>", arguments[0].toUpperCase()));
            return true;
        }

        this._plugin.GetKitsManager().DeleteKit(arguments[0]);

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessage(commandLabel, command, commandSender, null, "DeleteKit.Success")
                                                                                     .replace("<KIT>", arguments[0].toUpperCase()));
        return true;
    }
}
