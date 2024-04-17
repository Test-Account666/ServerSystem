package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand extends CommandUtils implements CommandExecutorOverload {

    public TeleportCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!this.plugin.getPermissions().hasPermission(commandSender, "tp.self", true)) {
                if (!this.plugin.getPermissions().hasPermission(commandSender, "tp.others", true)) {
                    this.plugin.log(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.NoPermissionInfo"))
                                             .replace("<SENDER>", commandSender.getName()));
                    commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission("tp.self || tp.others"));
                    return true;
                }
            }
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Tp"));
            return true;
        } else if (arguments.length == 1) {
            if ((!(commandSender instanceof Player))) {
                
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Tp"));
                return true;
            }
            if (!this.plugin.getPermissions().hasPermission(commandSender, "tp.self")) {
                var permission = this.plugin.getPermissions().getPermission("tp.self");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }
            var target = this.getPlayer(commandSender, arguments[0]);
            if (target == null) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
                return true;
            }
            if (target != commandSender)
                if (!this.plugin.getWantsTeleport().wantsTeleport(target)) {
                    
                    commandSender.sendMessage(
                            this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Tp.NoTeleportations"));
                    return true;
                }

            Teleport.teleport((Player) commandSender, target);

            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Tp.Self"));
            return true;
        } else if (!this.plugin.getPermissions().hasPermission(commandSender, "tp.others")) {
            var permission = this.plugin.getPermissions().getPermission("tp.others");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }
        var target1 = this.getPlayer(commandSender, arguments[0]);
        if (target1 == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }
        if (target1 != commandSender)
            if (!this.plugin.getWantsTeleport().wantsTeleport(target1)) {
                
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target1, "Tp.NoTeleportations"));
                return true;
            }
        var target2 = this.getPlayer(commandSender, arguments[1]);
        if (target2 == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[1]));
            return true;
        }
        if (target2 != commandSender)
            if (!this.plugin.getWantsTeleport().wantsTeleport(target2)) {
                
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target2, "Tp.NoTeleportations"));
                return true;
            }

        Teleport.teleport(target1, target2);

        
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command, commandSender, target1, "Tp.Others")
                                                                                     .replace("<TARGET2>", target2.getName())
                                                                                     .replace("<TARGET2DISPLAY>", target2.getDisplayName()));
        return true;
    }
}
