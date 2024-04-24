package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.Message;
import me.entity303.serversystem.utils.PermissionsChecker;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnvilCommand extends CommandUtils implements ICommandExecutorOverload {

    public AnvilCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!HasPermission(commandSender, this._plugin.GetMessages(), this._plugin.GetPermissions(), "anvil"))
            return true;

        this._plugin.GetVersionStuff().GetVirtualAnvil().OpenAnvil((Player) commandSender);
        return true;
    }

    static boolean HasPermission(CommandSender commandSender, Message messages, PermissionsChecker permissions, String anvil) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(messages.GetPrefix() + messages.GetOnlyPlayer());
            return false;
        }

        if (!permissions.HasPermission(commandSender, anvil)) {
            var permission = permissions.GetPermission(anvil);
            commandSender.sendMessage(messages.GetPrefix() + messages.GetNoPermission(permission));
            return false;
        }

        return true;
    }
}
