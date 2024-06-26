package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DeleteWarpCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public DeleteWarpCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "delwarp")) {
            var permission = this._plugin.GetPermissions().GetPermission("delwarp");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "DelWarp"));
            return true;
        }

        var name = arguments[0].toLowerCase();
        var warpManager = this._plugin.GetWarpManager();
        if (!warpManager.DoesWarpExist(name)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessage(commandLabel, command, commandSender, null,
                                                                                                     "DelWarp.WarpDoesntExists")
                                                                                         .replace("<WARP>", name.toUpperCase()));
            return true;
        }

        warpManager.DeleteWarp(name);

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessage(commandLabel, command, commandSender, null, "DelWarp.Success")
                                                                                     .replace("<WARP>", name.toUpperCase()));
        return true;
    }
}
