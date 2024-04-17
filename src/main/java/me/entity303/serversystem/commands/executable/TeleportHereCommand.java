package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TeleportHereCommand extends CommandUtils implements CommandExecutorOverload {

    public TeleportHereCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "tphere")) {
            var permission = this.plugin.getPermissions().getPermission("tphere");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (arguments.length == 0) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Tphere"));
            return true;
        }
        var target = this.getPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }
        if (!this.plugin.getWantsTeleport().wantsTeleport(target)) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Tphere.NoTeleportations"));
            return true;
        }

        Teleport.teleport(target, (Entity) commandSender);
        
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Tphere.Success"));
        return true;
    }
}
