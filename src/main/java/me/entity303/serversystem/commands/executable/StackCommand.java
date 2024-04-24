package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StackCommand extends CommandUtils implements ICommandExecutorOverload {

    public StackCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (AnvilCommand.HasPermission(commandSender, this._plugin.GetMessages(), this._plugin.GetPermissions(), "stack"))
                return true;
            ((Player) commandSender).getInventory().getItemInMainHand();
            ((Player) commandSender).getInventory().getItemInMainHand().setAmount(((Player) commandSender).getInventory().getItemInMainHand().getMaxStackSize());
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Stack.Success"));
        }
        return true;
    }
}
