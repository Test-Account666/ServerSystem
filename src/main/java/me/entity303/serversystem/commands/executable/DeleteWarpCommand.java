package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DeleteWarpCommand extends CommandUtils implements CommandExecutorOverload {

    public DeleteWarpCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "delwarp")) {
            var permission = this.plugin.getPermissions().getPermission("delwarp");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "DelWarp"));
            return true;
        }

        var name = arguments[0].toLowerCase();
        var warpManager = this.plugin.getWarpManager();
        if (!warpManager.doesWarpExist(name)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command, commandSender, null,
                                                                                                     "DelWarp.WarpDoesntExists")
                                                                                         .replace("<WARP>", name.toUpperCase()));
            return true;
        }

        warpManager.deleteWarp(name);

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command, commandSender, null, "DelWarp.Success")
                                                                                     .replace("<WARP>", name.toUpperCase()));
        return true;
    }
}
