package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportForceCommand extends CommandUtils implements CommandExecutorOverload {

    public TeleportForceCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (this.plugin.getPermissions().hasPermission(commandSender, "tpo.self", true) || this.plugin.getPermissions().hasPermission(commandSender, "tpo.others", true)) {
                
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Tpo"));
                return true;
            }
            this.plugin.log(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.NoPermissionInfo"))
                                     .replace("<SENDER>", commandSender.getName()));
            var permission = this.plugin.getPermissions().getPermission("tpo.self") + " || " + this.plugin.getPermissions().getPermission("tpo.others");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }
        if (arguments.length == 1) {
            if ((!(commandSender instanceof Player))) {
                
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Tpo"));
                return true;
            }
            if (!this.plugin.getPermissions().hasPermission(commandSender, "tpo.self")) {
                var permission = this.plugin.getPermissions().getPermission("tpo.self");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }
            var target = this.getPlayer(commandSender, arguments[0]);
            if (target == null) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
                return true;
            }

            Teleport.teleport((Player) commandSender, target);

            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Tpo.Self"));
            return true;
        }
        if (!this.plugin.getPermissions().hasPermission(commandSender, "tpo.others")) {
            var permission = this.plugin.getPermissions().getPermission("tpo.others");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }
        var target1 = this.getPlayer(commandSender, arguments[0]);
        if (target1 == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }
        var target2 = this.getPlayer(commandSender, arguments[1]);
        if (target2 == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[1]));
            return true;
        }

        Teleport.teleport(target1, target2);

        
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target1, "Tpo.Others").replace("<TARGET2>", target2.getName()));
        return true;
    }
}
