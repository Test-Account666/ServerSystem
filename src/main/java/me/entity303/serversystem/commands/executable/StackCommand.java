package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StackCommand extends CommandUtils implements CommandExecutorOverload {

    public StackCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
                return true;
            }
            if (!this.plugin.getPermissions().hasPermission(commandSender, "stack")) {
                var permission = this.plugin.getPermissions().getPermission("stack");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }
            ((Player) commandSender).getInventory().getItemInMainHand();
            ((Player) commandSender).getInventory().getItemInMainHand().setAmount(((Player) commandSender).getInventory().getItemInMainHand().getMaxStackSize());
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Stack.Success"));
        }
        return true;
    }
}
