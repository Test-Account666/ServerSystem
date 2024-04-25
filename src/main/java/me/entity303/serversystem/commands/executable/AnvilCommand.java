package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnvilCommand extends CommandUtils implements ICommandExecutorOverload {

    public AnvilCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        var messages = this._plugin.GetMessages();
        var permissions = this._plugin.GetPermissions();
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(messages.GetPrefix() + messages.GetOnlyPlayer());
            return true;
        }

        if (!permissions.HasPermission(commandSender, "anvil")) {
            var permission = permissions.GetPermission("anvil");
            commandSender.sendMessage(messages.GetPrefix() + messages.GetNoPermission(permission));
            return true;
        }

        this._plugin.GetVersionStuff().GetVirtualAnvil().OpenAnvil((Player) commandSender);
        return true;
    }

}
