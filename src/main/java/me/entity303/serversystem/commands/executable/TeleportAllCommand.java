package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportAllCommand extends CommandUtils implements CommandExecutorOverload {

    public TeleportAllCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player)) {
                
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "TpAll"));
                return true;
            }
            if (!this.plugin.getPermissions().hasPermission(commandSender, "tpall.self")) {
                var permission = this.plugin.getPermissions().getPermission("tpall.self");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }

            Bukkit.getOnlinePlayers().forEach(all -> Teleport.teleport(all, (Player) commandSender));
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "TpAll.Self"));
        } else if (this.plugin.getPermissions().hasPermission(commandSender, "tpall.others")) {
            var target = this.getPlayer(commandSender, arguments[0]);
            if (target != null) {
                Bukkit.getOnlinePlayers().forEach(all -> Teleport.teleport(all, target));
                var command1 = command.getName();
                target.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command1, commandSender, target, "TpAll.Self"));
                
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "TpAll.Others"));
            } else
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
        } else if (commandSender instanceof Player)
            if (this.plugin.getPermissions().hasPermission(commandSender, "tpall.self")) {
                Bukkit.getOnlinePlayers().forEach(all -> Teleport.teleport(all, (Player) commandSender));
                
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "TpAll.Self"));
            } else {
                var permission = this.plugin.getPermissions().getPermission("tpall.others");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            }
        else {
            var permission = this.plugin.getPermissions().getPermission("tpall.others");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
        }
        return true;
    }
}
