package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DeleteKitCommand extends CommandUtils implements CommandExecutorOverload {

    public DeleteKitCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "deletekit")) {
            var permission = this.plugin.getPermissions().getPermission("deletekit");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "DeleteKit"));
            return true;
        }

        if (!this.plugin.getKitsManager().doesKitExist(arguments[0])) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command, commandSender, null,
                                                                                                     "DeleteKit.DoesntExist")
                                                                                         .replace("<KIT>", arguments[0].toUpperCase()));
            return true;
        }

        this.plugin.getKitsManager().deleteKit(arguments[0]);

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command, commandSender, null, "DeleteKit.Success")
                                                                                     .replace("<KIT>", arguments[0].toUpperCase()));
        return true;
    }
}
