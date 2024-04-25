package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.Message;
import me.entity303.serversystem.utils.PermissionsChecker;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand extends CommandUtils implements ICommandExecutorOverload {

    public SetWarpCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        boolean result = false;
        Message messages = this._plugin.GetMessages();
        PermissionsChecker permissions = this._plugin.GetPermissions();
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(messages.GetPrefix() + messages.GetOnlyPlayer());
        } else if (!permissions.HasPermission(commandSender, "setwarp")) {
            var permission = permissions.GetPermission("setwarp");
            commandSender.sendMessage(messages.GetPrefix() + messages.GetNoPermission(permission));
        } else {
            result = true;
        }

        if (result)
            return true;
        if (arguments.length == 0) {
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "SetWarp"));
            return true;
        }
        var name = arguments[0].toLowerCase();
        var warpManager = this._plugin.GetWarpManager();
        if (warpManager.DoesWarpExist(name)) {
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "SetWarp.WarpAlreadyExists").replace("<WARP>", name.toUpperCase()));
            return true;
        }
        warpManager.AddWarp(name, ((Player) commandSender).getLocation());
        
        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "SetWarp.Success").replace("<WARP>", name.toUpperCase()));
        return true;
    }
}
