package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.Message;
import me.entity303.serversystem.utils.PermissionsChecker;
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
            boolean result = false;
            Message messages = this._plugin.GetMessages();
            PermissionsChecker permissions = this._plugin.GetPermissions();
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(messages.GetPrefix() + messages.GetOnlyPlayer());
            } else if (!permissions.HasPermission(commandSender, "stack")) {
                var permission = permissions.GetPermission("stack");
                commandSender.sendMessage(messages.GetPrefix() + messages.GetNoPermission(permission));
            } else {
                result = true;
            }

            if (result)
                return true;
            ((Player) commandSender).getInventory().getItemInMainHand();
            ((Player) commandSender).getInventory().getItemInMainHand().setAmount(((Player) commandSender).getInventory().getItemInMainHand().getMaxStackSize());
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Stack.Success"));
        }
        return true;
    }
}
