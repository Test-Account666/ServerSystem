package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand extends CommandUtils implements CommandExecutorOverload {

    public SetWarpCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (!this.plugin.getPermissions().hasPermission(commandSender, "setwarp")) {
            var permission = this.plugin.getPermissions().getPermission("setwarp");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }
        if (arguments.length == 0) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "SetWarp"));
            return true;
        }
        var name = arguments[0].toLowerCase();
        var warpManager = this.plugin.getWarpManager();
        if (warpManager.doesWarpExist(name)) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "SetWarp.WarpAlreadyExists").replace("<WARP>", name.toUpperCase()));
            return true;
        }
        warpManager.addWarp(name, ((Player) commandSender).getLocation());
        
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "SetWarp.Success").replace("<WARP>", name.toUpperCase()));
        return true;
    }
}
