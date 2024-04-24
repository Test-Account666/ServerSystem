package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
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
        if (AnvilCommand.HasPermission(commandSender, this._plugin.GetMessages(), this._plugin.GetPermissions(), "setwarp"))
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
