package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public SetWarpCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        var messages = this._plugin.GetMessages();
        var permissions = this._plugin.GetPermissions();
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(messages.GetPrefix() + messages.GetOnlyPlayer());
            return true;
        }

        if (!permissions.HasPermission(commandSender, "setwarp")) {
            var permission = permissions.GetPermission("setwarp");
            commandSender.sendMessage(messages.GetPrefix() + messages.GetNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "SetWarp"));
            return true;
        }

        var name = arguments[0].toLowerCase();
        var warpManager = this._plugin.GetWarpManager();
        if (warpManager.DoesWarpExist(name)) {

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, null,
                                                                                                       "SetWarp.WarpAlreadyExists")
                                                                                           .replace("<WARP>", name.toUpperCase()));
            return true;
        }
        warpManager.AddWarp(name, player.getLocation());

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                       .GetMessage(commandLabel, command, commandSender, null,
                                                                                                   "SetWarp.Success")
                                                                                       .replace("<WARP>", name.toUpperCase()));
        return true;
    }
}
