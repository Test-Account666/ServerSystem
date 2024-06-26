package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StackCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public StackCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            var messages = this._plugin.GetMessages();
            var permissions = this._plugin.GetPermissions();
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(messages.GetPrefix() + messages.GetOnlyPlayer());
                return true;
            }

            if (!permissions.HasPermission(commandSender, "stack")) {
                var permission = permissions.GetPermission("stack");
                commandSender.sendMessage(messages.GetPrefix() + messages.GetNoPermission(permission));
                return true;
            }

            player.getInventory().getItemInMainHand();
            player.getInventory()
                                    .getItemInMainHand()
                                    .setAmount(player.getInventory().getItemInMainHand().getMaxStackSize());

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Stack.Success"));
        }
        return true;
    }
}
